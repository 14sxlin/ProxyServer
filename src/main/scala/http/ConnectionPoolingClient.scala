package http

import connection.CloseWhenNotActive
import entity.response.Response
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.util.EntityUtils

/**
  * Created by linsixin on 2017/8/25.
  */
class ConnectionPoolingClient extends CloseWhenNotActive{

  private val cm = new PoolingHttpClientConnectionManager()
  cm.setMaxTotal(200)
  private val client = HttpClients
    .custom().setConnectionManager(cm)
    .build()

  def doRequest(request:HttpUriRequest,
                context: HttpClientContext,
                encoding:String = "utf8") : Response = {
    updateActiveTime()
    val httpResponse = client.execute(request,context)
    val entity = httpResponse.getEntity
    val response = Response(
      httpResponse.getStatusLine.toString,
      httpResponse.getAllHeaders.map(h => (h.getName, h.getValue)),
      entity match {
        case null => StringUtils.EMPTY
        case _ => EntityUtils.toString(entity,encoding).trim
      }
    )

    response
  }

  override def timeToClose(): Unit = {
    cm.closeExpiredConnections()
  }

  def close() : Unit = {
    client.close()
  }

}
