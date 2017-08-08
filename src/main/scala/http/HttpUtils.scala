package http

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
/**
  * Created by sparr on 2017/8/7.
  */
object HttpUtils {

  val logger : Logger = LoggerFactory.getLogger(getClass)

  /**
    * do get method
    * @param url url
    * @return (responseLine,headers,content)
    */
  def doGet(url:String) : (String,Array[(String,String)],String) =  {
    val httpClient = HttpClients.createDefault()
    val getMethod = new HttpGet(url)
    val response = httpClient.execute(getMethod)

    val responseLine = response.getStatusLine.toString
    val headers = response.getAllHeaders.map(header =>{
      (header.getName,header.getValue)
    })
    val content = EntityUtils.toString(response.getEntity)

    response.close()
    httpClient.close()

    logResponse(responseLine,headers, content.length)

    (responseLine,headers,content)
  }

  /**
    * do http request
    * @param url url
    * @param params params want to post , Array of (name,value) tuple
    * @return (responseLine,headers,content)
    */
  def doPost(url:String,params:Array[(String,String)]):
                            (String,Array[(String,String)],String)  = {

    val client = HttpClients.createDefault()
    val postRequest = new HttpPost(url)
    val nameValuePairs = for((name,value) <- params)
      yield new BasicNameValuePair(name, value)

    nameValuePairs.foreach((param) =>{
      logger.info(s"param < ${param.getName} : ${param.getValue} >")
    })

    postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs.toList))

    val response = client.execute(postRequest)

    val responseLine = response.getStatusLine.toString
    val headers = response.getAllHeaders.map(header =>{
      (header.getName,header.getValue)
    })
    val content = EntityUtils.toString(response.getEntity)

    response.close()
    client.close()

    logResponse(responseLine,headers, content.length)

    (responseLine,headers,content)
  }

  private def logResponse(responseLine:String,
                          headers:Array[(String,String)],
                          contentLength:Int) = {
    logger.info("status line : {}",responseLine)
    logger.info("headers : {}", headers)
    logger.info("body content length : {}",contentLength)
  }
}
