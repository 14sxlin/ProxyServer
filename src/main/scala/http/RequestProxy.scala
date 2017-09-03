package http

import constants.LoggerMark
import entity.response.Response
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/25.
  * This session proxy help to communicate
  * with ConnectionPoolingClient and holds
  * the session context.Since session context
  * is not thread safe. So RequestProxy is also
  * thread unsafe.
  */
class RequestProxy(private val connectionPoolingClient: ConnectionPoolingClient) {

  private val logger = LoggerFactory.getLogger(getClass)

  def doRequest(request:HttpUriRequest,context:HttpClientContext): Response = {
    logger.info(s"${LoggerMark.up} proxy do request : ${request.getRequestLine.toString}")
    connectionPoolingClient.doRequest(request,context)
  }

}
