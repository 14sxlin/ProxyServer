package http

import entity.response.Response
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext

/**
  * Created by linsixin on 2017/8/25.
  * This session proxy help to communicate
  * with ConnectionPoolingClient and holds
  * the session context.Since session context
  * is not thread safe. So RequestProxy is also
  * thread unsafe.
  */
class RequestProxy(private val connectionPoolingClient: ConnectionPoolingClient) {

  def doRequest(request:HttpUriRequest,context:HttpClientContext): Response = {
    connectionPoolingClient.doRequest(request,context)
  }

}
