package http

import java.util.concurrent.TimeUnit

import connection.ConnectionConstants
import entity.response.{BinaryResponse, Response, TextResponse}
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.util.EntityUtils
import utils.http.IOUtils

/**
  * Created by linsixin on 2017/8/25.
  */
class ConnectionPoolingClient {


  private val cm = new PoolingHttpClientConnectionManager()
  cm.setMaxTotal(ConnectionConstants.maxConnection)
  private val client = HttpClients
    .custom().setConnectionManager(cm)
    .build()

  def doRequest(request:HttpUriRequest,
                context: HttpClientContext,
                encoding:String = "utf8") : Response = {
    cm.closeIdleConnections(3,TimeUnit.SECONDS)
    val httpResponse = client.execute(request,context)
    val entity = httpResponse.getEntity
    val headers = httpResponse.getAllHeaders.map(h => (h.getName, h.getValue))

    if(isTextEntity(headers))
      TextResponse(
        httpResponse.getStatusLine.toString,
        headers,
        entity match {
          case null => StringUtils.EMPTY
          case _ => EntityUtils.toString(entity,encoding).trim
        }
      )
    else
      BinaryResponse(
        httpResponse.getStatusLine.toString,
        headers,
        entity match {
          case null => Array.emptyByteArray
          case _ =>
            IOUtils.dataFromInputStream(entity.getContent)
        }
      )
  }

  private def isTextEntity(headers:Array[(String,String)]) = {
    headers.exists(contentTypeHeader) &&
      headers.find(contentTypeHeader).get._2.contains("text")
  }

  private def contentTypeHeader (nameValue:(String,String)) :Boolean= {
    nameValue._1 == HttpHeaders.CONTENT_TYPE
  }

  def close() : Unit = {
    client.close()
  }

}
