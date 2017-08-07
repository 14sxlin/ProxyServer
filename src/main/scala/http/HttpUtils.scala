package http

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by sparr on 2017/8/7.
  */
object HttpUtils {

  val logger : Logger = LoggerFactory.getLogger(getClass)

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

    logger.info("status line : {}",response.getStatusLine)
    logger.info("headers : {}", headers)
    logger.info("body content length : {}",content.length)
    (responseLine,headers,content)
  }
}
