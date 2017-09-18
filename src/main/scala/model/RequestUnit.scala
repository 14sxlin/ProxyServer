package entity.request

import connection.ClientConnection
import constants.LoggerMark
import entity.response.Response
import filter.ResponseFilterChain
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/30.
  */
case class RequestUnit(key:String,
                       request:HttpUriRequest,
                       context:HttpClientContext){
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
