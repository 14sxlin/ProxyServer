import java.io.File
import java.security.KeyStore

import org.apache.http.HttpHost
import org.apache.http.client.methods.HttpGet
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.ssl.{NoopHostnameVerifier, SSLConnectionSocketFactory}
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContexts
import org.apache.http.util.EntityUtils

/**
  * Created by linsixin on 2017/9/4.
  */
object _8443ProxyRun extends App{

  val context = SSLContexts.custom()
    .build()
  val sslFactory = new SSLConnectionSocketFactory(
    context,
    NoopHostnameVerifier.INSTANCE)
  val registry = RegistryBuilder.create[ConnectionSocketFactory]()
    .register("https",sslFactory)
    .build()
  val poolingHttpClientConnectionManager
  = new PoolingHttpClientConnectionManager(registry)

  val proxy = new HttpHost("127.0.0.1",689,"https")
  val client = HttpClients.custom()
    .setConnectionManager(poolingHttpClientConnectionManager)
    .setProxy(proxy)
    .build()

  val httpGet = new HttpGet("https://gg.sparrowxin.com:8443")
  println(s"request for ${httpGet.getURI}")
  val response = client.execute(httpGet)
  println(response.getStatusLine.toString)
  println(EntityUtils.toString(response.getEntity).trim)
  //It seems that this method will has some "/r/n" in front of body
  response.close()
}
