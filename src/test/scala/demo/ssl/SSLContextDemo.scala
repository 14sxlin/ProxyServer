package demo.ssl

import java.security.SecureRandom
import javax.net.ssl.SSLContext

/**
  * Created by linsixin on 2017/9/5.
  */
object SSLContextDemo extends App{
  val tManager = new X509TrustManagerImp()
  val sslContext = SSLContext.getInstance("SSL")
  val random = new SecureRandom
  sslContext.init(null,Array(tManager),random)
  println(s"default protocol : ${sslContext.getProtocol}")
  val sslEngine = sslContext.createSSLEngine()
  println(s"support protocols : \n" +
    s"${sslEngine.getSupportedProtocols.mkString("\n")}")
  println(s"enable protocols : \n" +
    s"${sslEngine.getEnabledProtocols.mkString("\n")}")
  println(s"support cypher : \n" +
    s"${sslEngine.getSupportedCipherSuites.mkString("\n")}")
  println(s"enable cypher : \n" +
    s"${sslEngine.getEnabledCipherSuites.mkString("\n")}")
}
