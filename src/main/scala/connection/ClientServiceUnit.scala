package connection

import constants.{ConnectionConstants, LoggerMark}
import entity.response.Response
import filter.ResponseFilterChain
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpHeaders
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/29.
  * When put and get service unit into
  * or from pool, updateActive() will
  * be call.
  */
case class ClientServiceUnit(clientConnection: ClientConnection,
                             context:HttpClientContext) extends ServiceUnit{
  override protected val idleThreshold: Long = ConnectionConstants.idleThreshold

  override def closeWhenNotActive(): Unit = {
    clientConnection.closeAllResource()
  }
}

object ClientServiceUnit{
  private val logger = LoggerFactory.getLogger(getClass)

  def onSuccess(client:ClientConnection,
                responseFilterChain: ResponseFilterChain.type
                      = ResponseFilterChain ) : Response => Unit = {
    response =>
      val filtedResponse = responseFilterChain.handle(response)
      val data = filtedResponse.mkHttpBinary()
      val content = filtedResponse.mkHttpString()
      val min = Math.min(content.length,500)
      logger.info(s"${LoggerMark.down} \n" +
        s"${content.substring(0,min)} \n" +
        s"${data.length} to client")
      client.writeBinaryData(data)
  }
}
