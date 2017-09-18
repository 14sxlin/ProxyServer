package connection.dispatch

import connection.ClientConnection
import connection.pool.ClientContextUnitPool
import constants.LoggerMark
import entity.response.Response
import filter.ResponseFilterChain
import model.{ContextUnit, RequestUnit}
import org.apache.http.client.methods.HttpUriRequest
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/25.
  */
class RequestDispatcher(pool:ClientContextUnitPool) {

  protected val logger : Logger = LoggerFactory.getLogger(getClass)

  def addNewContextUnit(key:String, unit:ContextUnit):Unit = {
    if(!pool.containsKey(key))
      pool.put(key,unit)
    else logger.warn(s"$key has existed in pool")
  }

  def removeExistContextUnit(key:String):Unit = {
    if(pool.containsKey(key))
      pool.remove(key)
    else logger.warn("try to remove service unit with" +
      s"key $key but it doesn't exist")
  }

  def containsKey(key:String):Boolean = {
    pool.containsKey(key)
  }

  /**
    * use context in ContextPool to build
    * a request unit
    * @param key hash of request and connection {@see utils.HashUtils}
    * @param request HttpUriRequest
    * @return request unit that should be put into request queue
    */
  def buildRequestUnit(key:String,
                       request:HttpUriRequest):RequestUnit = {
    pool.get(key) match {
      case None =>
        throw new IllegalArgumentException(s"There must be a ContextUnit whit key $key")
      case Some(serviceUnit) =>
        RequestUnit(
          key,
          request,
          serviceUnit.context,
          onSuccess(serviceUnit.clientConnection)
        )
    }

  }


  protected def onSuccess(client:ClientConnection,
                responseFilterChain: ResponseFilterChain.type
                = ResponseFilterChain ) : Response => Unit = {
    response =>
      val filtedResponse = responseFilterChain.handle(response)
      val data = filtedResponse.mkHttpBinary()
      logResponse(filtedResponse,data.length)
      client.writeBinaryData(data)
  }


  protected def logResponse(response: Response,length:Int):Unit = {
    val content = response.mkHttpString()
    val min = Math.min(content.length,500)
    logger.info(s"${LoggerMark.down} \n" +
      s"${content.substring(0,min)} \n" +
      s"$length to client")
  }

}
