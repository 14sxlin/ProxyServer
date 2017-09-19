package demo.ssl

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
  * Created by linsixin on 2017/9/5.
  */
class X509TrustManagerImp extends X509TrustManager{

  override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {

  }

  override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {

  }

  override def getAcceptedIssuers: Array[X509Certificate] = {
    null
  }
}
