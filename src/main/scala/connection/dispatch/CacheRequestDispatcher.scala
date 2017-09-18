package connection.dispatch

import connection.ClientConnection
import connection.pool.ClientContextUnitPool
import constants.LoggerMark
import entity.response.Response
import filter.ResponseFilterChain
import model.{CacheContextUnit, CacheUnit, RequestUnit}
import org.apache.http.client.methods.HttpUriRequest

/**
  * Created by linsixin on 2017/9/16.
  */
class CacheRequestDispatcher(pool:ClientContextUnitPool) extends RequestDispatcher(pool){

  override def buildRequestUnit(key:String,
                                request:HttpUriRequest):RequestUnit = {
    pool.get(key) match {
      case None =>
        throw new IllegalArgumentException(s"There must be a ContextUnit whit key $key")
      case Some(contextUnit) if contextUnit.isInstanceOf[CacheContextUnit] =>
        val cacheContextUnit = contextUnit.asInstanceOf[CacheContextUnit]
        RequestUnit(
          key,
          request,
          cacheContextUnit.context,
          onCacheSuccess(
            cacheContextUnit.clientConnection,
            cacheContextUnit.cacheUnit
          )
        )
      case Some(contextUnit) =>
        RequestUnit(
          key,
          request,
          contextUnit.context,
          onSuccess(contextUnit.clientConnection)
        )
    }

  }

  def onCacheSuccess(client:ClientConnection,
                cacheUnit: CacheUnit,
                responseFilterChain: ResponseFilterChain.type
                = ResponseFilterChain ) : Response => Unit = {
    response =>
      val filtedResponse = responseFilterChain.handle(response)

      if(cacheUnit.hasFilled)
        fillOrProlongWhen200And304(response,cacheUnit)
      else fillResponse(response,cacheUnit)

      val data = filtedResponse.mkHttpBinary()
      logResponse(filtedResponse,data.length)
      client.writeBinaryData(data)
  }

  private def fillOrProlongWhen200And304(response: Response,cacheUnit: CacheUnit) : Unit = {
    if(response.getStatusCode == "304") {
      logger.info(s"${LoggerMark.cache} 304  prolong cache, prolong response")
      cacheUnit.prolong()
    }else if(response.getStatusCode == "200"){
      logger.info(s"${LoggerMark.cache} status 200")
      cacheUnit.fill(response)
    }else throw new Exception(s"unknown respone in cache : ${response.mkHttpString()}")

  }

  private def fillResponse(response: Response,cacheUnit: CacheUnit) : Unit = {
    logger.info(s"${LoggerMark.cache} fill response")
    cacheUnit.fill(response)
  }

}
