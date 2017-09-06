import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.{Socket, URL}
import java.security.{KeyStore, PrivateKey}
import javax.crypto.Cipher
import javax.net.ssl.HttpsURLConnection

import utils.{HexUtils, HttpUtils, IOUtils, SSLContextFactory}

/**
  * Created by linsixin on 2017/9/6.
  */
object _8443SimpleProxy extends App{

  val port = 689
  val context = SSLContextFactory.getContext("D:/keys/tomcat","123456")
  val ssLServerSocketFactory = context.getServerSocketFactory
  val sslServerSocket = ssLServerSocketFactory.createServerSocket(port)
  println(s"begin accept at $port")
  val sslSocket = sslServerSocket.accept()
  sslServerSocket.close()

  val socketIn  = new BufferedInputStream(sslSocket.getInputStream)
  val socketOut = sslSocket.getOutputStream

  val websiteSocket = new Socket("gg.sparrowxin.com",8443)
  val websiteIn = new BufferedInputStream(websiteSocket.getInputStream)
  val websiteOut = websiteSocket.getOutputStream

  val transferThread1 = new Thread(new Runnable {
    override def run(): Unit = {
      val buffer = new Array[Byte](2048)
      var length = 0
      while(length != -1){
        length = socketIn.read(buffer)
        println(s"request >>>>>>>>>> $length")
        websiteOut.write(buffer.slice(0,length))
      }
    }
  })



  val transferThread2 = new Thread(new Runnable {
    override def run(): Unit = {
      val buffer = new Array[Byte](2048)
      var length = 0
      while(length != -1){
        length = websiteIn.read(buffer)
        println(s"response <<<<<<<<< $length")
        socketOut.write(buffer.slice(0,length))
      }
    }
  })


  def readRequestFromClient() = {
    val content = IOUtils.dataFromInputStream(socketIn)
    println("request: " + new String(content))
    println(HexUtils.toHex(content))
    content
  }

  def responseCONNECT200() = {
    socketOut.write(HttpUtils.establishConnectInfo.getBytes())
    socketOut.flush()
  }

  def sendRequestToServerAndResponseClient(url:String, request:Array[Byte]) = {
    val httpsConnection = new URL(url).openConnection().asInstanceOf[HttpsURLConnection]
    //  val websiteSSLSocket = context.getSocketFactory.createSocket("127.0.0.1",8443)
    httpsConnection.setSSLSocketFactory(context.getSocketFactory)
    httpsConnection.setDoOutput(true)
    httpsConnection.setConnectTimeout(1000)
    httpsConnection.setReadTimeout(2000)
    httpsConnection.connect()

    val websiteOut = new BufferedOutputStream(httpsConnection.getOutputStream)
    websiteOut.write(request)
    websiteOut.flush()

    val websiteIn = new BufferedInputStream(httpsConnection.getInputStream)
    val response = IOUtils.dataFromInputStream(socketIn)
    println(new String(response) + "\n" + response.mkString)
    socketOut.write(response)
    socketOut.flush()

    websiteIn.close()
    websiteOut.close()
    httpsConnection.disconnect()
  }

  val connectRequest = readRequestFromClient()
  responseCONNECT200()
//  val request = readRequestFromClient()
//  sendRequestToServerAndResponseClient("https://gg.sparrowxin.com:8443",request)

  transferThread1.start()
  transferThread2.start()

  transferThread1.join()
  transferThread2.join()

  sslSocket.close()


  def decrypt(encryptData:Array[Byte]) = {
    val keyStore = SSLContextFactory.getKeyStore("D:/keys/tomcat","123456")
    val privateKey = keyStore.getKey("tomcat","123456".toCharArray).asInstanceOf[PrivateKey]
    println(s"algorithm : ${privateKey.getAlgorithm}")
    val cipher = Cipher.getInstance("RSA/ECB/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE,privateKey)
    val decryptContent = cipher.doFinal(encryptData)
    println(s"decrypt:  ${new String(decryptContent)}")

  }
}
