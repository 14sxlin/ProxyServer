package utils

import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
  * Created by linsixin on 2017/9/6.
  */
object _8443Run extends App {

  def useSSLSocket():Unit = {
    val request = "GET / HTTP/1.1\nHost: gg.sparrowxin.com:8443\nConnection: keep-alive\nCache-Control: max-age=0\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\nUpgrade-Insecure-Requests: 1\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.1.3397.16 Safari/537.36\nAccept-Encoding: gzip, deflate\nAccept-Language: zh-CN,zh;q=0.8"
    val requestBytes = request.getBytes()
    val context = SSLContextFactory.getContext("D:/keys/tomcat", "123456")
    val ssLServerSocketFactory = context.getServerSocketFactory
    val websiteSSLSocket = context.getSocketFactory.createSocket("gg.sparrowxin.com", 8443)
    println(s"${websiteSSLSocket.getLocalPort} -> ${websiteSSLSocket.getPort}")
    println("write request")
    val websiteOut = new BufferedOutputStream(websiteSSLSocket.getOutputStream)
    websiteOut.write(requestBytes)
    println(HexUtils.toHex(requestBytes))
    websiteOut.flush()
    println("read response")

    val websiteIn = new BufferedInputStream(websiteSSLSocket.getInputStream)

    val content = IOUtils.dataFromResponseInputStream(websiteIn)
    println(new String(content))

    websiteSSLSocket.close()
  }

  def useHttpsURLConnection(urlString:String): Unit ={
//    val context =
    val url = new URL(urlString)
    val httpsConnection = url.openConnection().asInstanceOf[HttpsURLConnection]
    httpsConnection.setConnectTimeout(1000)
    httpsConnection.setReadTimeout(2000)
    httpsConnection.connect()
    val in = new BufferedInputStream(httpsConnection.getInputStream)
    val content = IOUtils.dataFromResponseInputStream(in)
    println(s"response :\n ${new String(content)}")
    in.close()
    httpsConnection.disconnect()
  }

  println("use HttpsConnection")
  val url = "https://gg.sparrowxin.com:8443"
  useHttpsURLConnection(url)

  println("use SSLSocket")
  useSSLSocket()
}
