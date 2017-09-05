package mock.client

import http.RequestProxy
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPost, HttpUriRequest}
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import utils.IOUtils

import scala.collection.JavaConversions._


/**
  * Created by linsixin on 2017/8/5.
  */
class HttpClientMock {

  private val logger = LoggerFactory.getLogger(getClass)


  val localPostUri = "http://localhost:8080/LoginDemo/login.do"
  val targetHost = "localhost"
  val targetPort = "8080"
  val proxyHost = "localhost"
  val proxyPort = 689

  def doGet(url:String) : String =  {
    val httpClient = HttpClients.createDefault()
    val getMethod = new HttpGet(url)
    val response = httpClient.execute(getMethod)
    logger.info("status line : {}",response.getStatusLine)
    val content = EntityUtils.toString(response.getEntity)
    response.close()
    httpClient.close()

    content
  }

  def doPost(url:String,attrs:Array[String],vals:Array[String]): String = {

    val client = HttpClients.createDefault()
    val postRequest = new HttpPost(url)
    val params = for((name,value) <- attrs.zip(vals))
      yield new BasicNameValuePair(name, value)

    for( param <- params)
      logger.info(" Param  < " + param.getName + " , "+ param.getValue+" >")
    postRequest.setEntity(new UrlEncodedFormEntity(params.toList))

    val response = client.execute(postRequest)

    for( header <- response.getAllHeaders)
      logger.info("{}",header)

    val content = EntityUtils.toString(response.getEntity)

    response.close()
    client.close()

    content
  }

  def doGetByProxyWithHttp(proxyHost:String,
                           proxyPort:Int,
                           uri:String) : String =  {
    val client = HttpClients.createDefault()

    val proxy = new HttpHost(proxyHost,proxyPort,"http")
    val config = RequestConfig.custom()
                    .setProxy(proxy).build()
    val request = new HttpGet(uri)
    request.setConfig(config)

    val response = client.execute(request)

    logger.info("response status : {}",response.getStatusLine)
    for( header <- response.getAllHeaders)
      logger.info("{}",header)

    val entity = response.getEntity
    val content = IOUtils.dataFromInputStream(entity.getContent)
//    FileUtils.save2File("logs/test.jpg",content)
//    response.close()
//    client.close()

    new String(content,"utf-8")
  }


  def doPostByProxyWithHttp(proxyHost:String,
                            proxyPort:Int,
                            uri:String,
                            params:Array[(String,String)]): String ={
    val client = HttpClients.createDefault()

    val proxy = new HttpHost(proxyHost,proxyPort,"http")
    val config = RequestConfig.custom()
      .setProxy(proxy).build()
    val request = new HttpPost(uri)
    request.setConfig(config)

    val nameValuePairs = for((name,value) <- params)
      yield new BasicNameValuePair(name, value)

    nameValuePairs.foreach((param) =>{
      logger.info(s"param < ${param.getName} : ${param.getValue} >")
    })

    request.setEntity(new UrlEncodedFormEntity(nameValuePairs.toList))

    val response = client.execute(request)

    logger.info("response status : {}",response.getStatusLine)
    for( header <- response.getAllHeaders)
      logger.info("{}",header)

    val content = EntityUtils.toString(response.getEntity)
    response.close()
    client.close()

    content

  }

  def doRequestProxyInPool(request:HttpUriRequest,
                         proxy:RequestProxy,
                         context:HttpClientContext
                        ) : String =  {
    val response = proxy.doRequest(request,context)
    logger.info("response status : {}",response.firstLine)
    response.mkHttpString()
  }

  def buildProxyRequest(proxyHost:String,
                        proxyPort:Int,
                        request:HttpUriRequest):HttpUriRequest = {
    val proxy = new HttpHost(proxyHost,proxyPort,"http")
    val config = RequestConfig.custom()
      .setProxy(proxy).build()
    request match {
      case r:HttpGet =>
        r.setConfig(config)
      case r:HttpPost =>
        r.setConfig(config)
    }
    request
  }

}
