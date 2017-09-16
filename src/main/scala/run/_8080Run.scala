package run

import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.{HttpURLConnection, Socket, URL}

import utils.{HexUtils, IOUtils}

/**
  * Created by linsixin on 2017/9/6.
  */
object _8080Run extends App{

  val url = "http://gg.sparrowxin.com:8080"

  def useSocket() : Unit = {
    val request = "GET / HTTP/1.1\nHost: gg.sparrowxin.com:8443\nConnection: keep-alive\nCache-Control: max-age=0\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\nUpgrade-Insecure-Requests: 1\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.1.3397.16 Safari/537.36\nAccept-Encoding: gzip, deflate\nAccept-Language: zh-CN,zh;q=0.8"
    val requestBytes = request.getBytes()
    val websiteSSLSocket = new Socket("gg.sparrowxin.com",8080)
    println("" + websiteSSLSocket.getLocalPort + " -> " + websiteSSLSocket.getPort)
    println("write request")
    val websiteOut = new BufferedOutputStream(websiteSSLSocket.getOutputStream)
    websiteOut.write(requestBytes)
    println(HexUtils.toHex(requestBytes))
    websiteOut.flush()
    println("read response")

    val websiteIn = new BufferedInputStream(websiteSSLSocket.getInputStream)
    val content = IOUtils.dataFromResponseInputStream(websiteIn)
    println(new String(content))

    websiteIn.close()
    websiteOut.close()
    websiteSSLSocket.close()
  }

  def useHttpConnection(urlString:String) : Unit = {
    val url = new URL(urlString)
    val httpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
    httpURLConnection.connect()
    val in = new BufferedInputStream(httpURLConnection.getInputStream)
    val content = IOUtils.dataFromResponseInputStream(in)
    println(s"response :\n ${new String(content)}")

    in.close()
    httpURLConnection.disconnect()
  }
  println("use HttpURLConnection")
  useHttpConnection(url)

  println("use socket")
  useSocket() // this won't get response
}
