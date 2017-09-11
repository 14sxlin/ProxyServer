package http

import java.util.concurrent.TimeUnit

import constants.ConnectionConstants
import entity.response.{BinaryResponse, Response, TextResponse}
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.util.EntityUtils
import org.apache.http.{HttpHeaders, HttpResponse}
import utils.IOUtils

/**
  * Created by linsixin on 2017/8/25.
  */
class ConnectionPoolClient {

  protected val cm = new PoolingHttpClientConnectionManager()
  import constants.Timeout._
  protected val config : RequestConfig =
    RequestConfig.custom()
        .setConnectionRequestTimeout(connectionRequestTimeout)
        .setConnectTimeout(connectTimeout)
        .setSocketTimeout(readTimeout)
        .build()
  cm.setMaxTotal(ConnectionConstants.maxConnection)
  protected val client : CloseableHttpClient =
    HttpClients.custom()
        .setConnectionManager(cm)
        .setDefaultRequestConfig(config)
        .build()


  def doRequest(request:HttpUriRequest,
                context: HttpClientContext) : Response = {
    val httpResponse = client.execute(request,context)
    val response = adapt(httpResponse)
    httpResponse.close()
    response
  }

  protected def adapt(httpResponse:HttpResponse):Response = {
    val entity = httpResponse.getEntity
    val headers = httpResponse.getAllHeaders.map(h => (h.getName, h.getValue))

    var response :Response = null
    def getCharset : Option[String] = {
      var charset = "utf8"
      headers.find(nameValue => {
        nameValue._1 == HttpHeaders.CONTENT_TYPE
      }) match {
        case None => None
        case Some((_,contentType)) =>
          charset = StringUtils.substringAfter(contentType,"charset=")
          if(charset.isEmpty)
            None
          else
            Some(charset)
      }
    }
    val charset = getCharset
    if(isTextEntity(headers) && charset.isDefined) {
      response =
        TextResponse(
          httpResponse.getStatusLine.toString,
          headers,
          entity match {
            case null => StringUtils.EMPTY
            case _ => EntityUtils.toString(entity,charset.get).trim
          }
        )
    }
    else {
      response =
        BinaryResponse(
          httpResponse.getStatusLine.toString,
          headers,
          entity match {
            case null => Array.emptyByteArray
            case _ =>
              IOUtils.dataFromResponseInputStream(entity.getContent)
          }
        )
    }
    response
  }

  protected def isTextEntity(headers:Array[(String,String)]): Boolean = {
    headers.exists(contentTypeHeader) &&
      headers.find(contentTypeHeader).get._2.contains("text")
  }

  protected def contentTypeHeader (nameValue:(String,String)) :Boolean= {
    nameValue._1 == HttpHeaders.CONTENT_TYPE
  }

  def close() : Unit = {
    client.close()
  }

  /**
    *
    * @param time time in second
    */
  def closeIdleConnection(time:Int):Unit = {
    cm.closeExpiredConnections()
    cm.closeIdleConnections(time,TimeUnit.SECONDS)
  }

}
