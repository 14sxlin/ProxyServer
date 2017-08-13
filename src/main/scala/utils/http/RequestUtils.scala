package utils.http

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.{HttpURLConnection, URL}

import entity.{Request, Response}
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
/**
  * Created by linsixin on 2017/8/7.
  */
object RequestUtils {

  val logger : Logger = LoggerFactory.getLogger(getClass)

  /**
    * send get request by HttpClient
    * @param request
    * @return
    */
  def doGetByHttpClient(request:Request) : Response =  {
    val httpClient = HttpClients.createDefault()
    val httpGet = new HttpGet(request.uri)
    val response = httpClient.execute(httpGet)

    val responseLine = response.getStatusLine.toString
    val headers = response.getAllHeaders.map(header =>{
      (header.getName,header.getValue)
    })
    val content = EntityUtils.toString(response.getEntity)

    response.close()
    httpClient.close()

    logResponse(responseLine,headers, content.length)

    Response(responseLine,headers,content)
  }


  /**
    * send post request by HttpClient
    * @param request
    * @param params
    * @return
    */
  def doPostByHttpClient(request: Request,
                         params:Array[(String,String)]):Response = {

    val client = HttpClients.createDefault()
    val postRequest = new HttpPost(request.uri)
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

    Response(responseLine,headers,content)
  }

  private def logResponse(responseLine:String,
                          headers:Array[(String,String)],
                          contentLength:Int) = {
    logger.info("status line : {}",responseLine)
    logger.info("headers : {}", headers)
    logger.info("body content length : {}",contentLength)
  }

  /**
    * directly send data from client to server using http connection
    */
  def sendDataBySocket(url:String,data:String) = {
    val con = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    con.setDoOutput(true)
    con.connect()
    val writer = new PrintWriter(con.getOutputStream)
    writer.append(data)
    writer.flush()

    val reader = new BufferedReader(new InputStreamReader(con.getInputStream))
    var line = reader.readLine()
    while(line != null){
      logger.debug(s"response : $line")
      line = reader.readLine()
    }

    writer.close()
    con.disconnect()

    Response("",null,"")
  }

}
