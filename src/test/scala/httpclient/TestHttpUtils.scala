package httpclient

import java.security.KeyStore
import javax.net.ssl.{SSLContext, SSLServerSocketFactory}

import org.apache.http.HttpHost
import org.apache.http.client.methods.HttpGet
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.ssl.{NoopHostnameVerifier, SSLConnectionSocketFactory}
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContexts
import org.apache.http.util.EntityUtils
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/8/19.
  */
class TestHttpUtils extends FunSuite {
  test("get") {
    val httpGet = new HttpGet("http://localhost:9000")
    val client = HttpClients.createDefault()
    val res = client.execute(httpGet)
    assert(res.getStatusLine.toString != null)
    res.close()
    client.close()
  }

  test("https"){
//    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType)
    val context = SSLContexts.custom()
//        .loadKeyMaterial("D:/keys/tomcat","123456".toCharArray)
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
    val response = client.execute(httpGet)
    println(response.getStatusLine.toString)
    println(EntityUtils.toString(response.getEntity).trim)
    //It seems that this method will has some "/r/n" in front of body
    response.close()
  }
}
