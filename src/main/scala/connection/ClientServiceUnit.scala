package connection

import constants.LoggerMark
import entity.response.Response
import filter.ResponseFilterChain
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/29.
  */
case class ClientServiceUnit(clientConnection: ClientConnection,
                             context:HttpClientContext) extends ServiceUnit{
  override protected val idleThreshold: Long = ConnectionConstants.idleThreshold + 8000L

  override def closeWhenNotActive(): Unit = {
    clientConnection.closeAllResource()
  }
}

object ClientServiceUnit{
  private val logger = LoggerFactory.getLogger(getClass)

  def writeResponse(client:ClientConnection,
                    responseFilterChain: ResponseFilterChain.type
                      = ResponseFilterChain ) : Response => Unit = {
    response =>
      val data = responseFilterChain.handle(response)
        .mkHttpBinary()
      val content = response.mkHttpString()
      val minLen = Math.min(content.length,1000)
      logger.info(s"${LoggerMark.down} \n" +
        s"${content.substring(0,minLen)}\n" +
        s"${data.length} to client")
      client.writeBinaryData(data)
  }
}
