package demo.ssl

import java.io.BufferedInputStream

import utils.{HexUtils, HttpUtils, IOUtils, SSLContextFactory}

/**
  * Created by linsixin on 2017/9/5.
  */
object SSLServerRun extends App{

  val port = 689

  val context = SSLContextFactory.getContext("D:/keys/tomcat","123456")

//  val ssLServerSocketFactory = SSLServerSocketFactory.getDefault.asInstanceOf[SSLServerSocketFactory]
  val ssLServerSocketFactory = context.getServerSocketFactory
  val sslServerSocket = ssLServerSocketFactory.createServerSocket(port)
  println(s"begin accept at $port")
  val sslSocket = sslServerSocket.accept()
  sslServerSocket.close()

  val socketIn  = new BufferedInputStream(sslSocket.getInputStream)
  val socketOut = sslSocket.getOutputStream
  var content = IOUtils.dataFromInputStream(socketIn)
  println("first: \n" + new String(content))
  println(HexUtils.toHex(content))
  socketOut.write(HttpUtils.establishConnectInfo.getBytes())
  socketOut.flush()

  content = IOUtils.dataFromInputStream(socketIn) //why messy code again
  println(s"content :\n" + new String(content))
  println(HexUtils.toHex(content))

  val websiteSSLSocket = context.getSocketFactory.createSocket("127.0.0.1",8443)
  val websiteOut = websiteSSLSocket.getOutputStream
  websiteOut.write(content)
  websiteOut.flush()

  val websiteIn = websiteSSLSocket.getInputStream
  content = IOUtils.dataFromInputStream(socketIn)
  println(new String(content) + "\n" + content.mkString)
  socketOut.write(content)

  websiteSSLSocket.close()
  sslSocket.close()
}
