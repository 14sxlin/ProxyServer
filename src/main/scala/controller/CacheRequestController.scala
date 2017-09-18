package controller

import java.util.concurrent.ArrayBlockingQueue

import cache.CacheHandler
import connection.ClientConnection
import connection.dispatch.CacheRequestDispatcher
import constants.LoggerMark
import entity.request._
import model.{CacheContextUnit, RequestUnit}
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory
import utils.HashUtils

/**
  * Created by linsixin on 2017/9/16.
  */
class CacheRequestController(requestDispatcher:CacheRequestDispatcher,
                             requestQueue: ArrayBlockingQueue[RequestUnit],
                             cacheHandler: CacheHandler) extends RequestController(requestDispatcher,requestQueue){

  private val logger = LoggerFactory.getLogger(getClass)

  override protected def processRequest(request: Request, client: ClientConnection): Unit = {
    request match {
      case r: HeaderRecognizedRequest =>
        if(processCacheRequestAndDecideContinueProcess(r,client))
          super.processRequest(request,client)
      case _ =>
        super.processRequest(request,client)
    }
  }

  /**
    *
    * @return should continue process
    */
  protected def processCacheRequestAndDecideContinueProcess(request: HeaderRecognizedRequest, clientConnection: ClientConnection): Boolean = {
    logger.info(s"${LoggerMark.cache} cache handler do ")
    cacheHandler.handle(request) match {
      case responseContainer: ResponseCachedRequest =>
        clientConnection.writeBinaryData(responseContainer.getResponseBinary())
        false
      case requestContainer: ValidateRequest =>
        val request = requestContainer.request
        val hash = HashUtils.getHash(clientConnection,request)
        super.processGetOrPostRequest(hash,request,clientConnection)
        false
      case _ => true
    }
  }


  override def createAndPutContextUnit(hash:String,
                                       httpUriRequest: HttpUriRequest,
                                       clientConnection: ClientConnection): Unit = {
    val absUri = getAbsoluteUri(httpUriRequest)
    cacheHandler.getCache(absUri) match {
      case Some(cache) =>
        logger.info(s"${LoggerMark.cache} absUri : $absUri")
        val cacheContextUnit = new CacheContextUnit(
          clientConnection,
          HttpClientContext.create(),
          cache
        )
        requestDispatcher.addNewContextUnit(hash,cacheContextUnit)
        val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
        requestQueue.put(requestUnit)
      case None =>
        super.createAndPutContextUnit(hash,httpUriRequest,clientConnection)
    }

  }

  private def getAbsoluteUri(httpUriRequest: HttpUriRequest) = {
    httpUriRequest.getAllHeaders.find( _.getName == HttpHeaders.HOST) match {
      case Some(host) => host + httpUriRequest.getURI.toString
      case None => httpUriRequest.getURI.toString
    }
  }

}
