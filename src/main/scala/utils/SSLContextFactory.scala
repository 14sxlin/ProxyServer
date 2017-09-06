package utils

import java.io.{BufferedInputStream, File, FileInputStream}
import java.security.cert.CertificateFactory
import java.security.{KeyStore, PrivateKey, PublicKey, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext}

import demo.ssl.X509TrustManagerImp

/**
  * Created by linsixin on 2017/9/6.
  */
object SSLContextFactory {

  def getKeyStore(keyStorePath:String,password: String):KeyStore = {
    println("key store default type: " + KeyStore.getDefaultType)
    val keystore = KeyStore.getInstance(KeyStore.getDefaultType)
    val keystoreFile = new File(keyStorePath)
    val in = new FileInputStream(keystoreFile)
    keystore.load(in,password.toCharArray)
    in.close()
    keystore
  }

  def getContext(keyStorePath:String,password:String) : SSLContext= {
    val keystore = getKeyStore(keyStorePath,password)
    val defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm
    println("key manager default factory algorithm :" + defaultAlgorithm)
    val keyManagerFactory = KeyManagerFactory.getInstance(defaultAlgorithm)
    keyManagerFactory.init(keystore,password.toCharArray)

    val context = SSLContext.getInstance("SSL")
    context.init(
      keyManagerFactory.getKeyManagers,
      Array(new X509TrustManagerImp),
      new SecureRandom())
    context
  }

  def getPrivateKey(keyStore: KeyStore,alias:String,password:String):PrivateKey = {
    val privateKey =
      keyStore.getKey(alias,password.toCharArray).asInstanceOf[PrivateKey]
    println(s"private key algorithm : ${privateKey.getAlgorithm}")
    privateKey
  }


  def getPublicKey(crtPath:String):PublicKey = {
    val certificateFactory = CertificateFactory.getInstance("X.509")
    val crtFile = new File(crtPath)
    val in = new FileInputStream(crtFile)
    val certificate = certificateFactory.generateCertificate(in)
    in.close()
    val pubKey = certificate.getPublicKey
    println(s"public key algorithm : ${pubKey.getAlgorithm}")
    pubKey
  }


}
