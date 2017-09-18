package cache

import constants.LoggerMark
import entity.request.{HeaderRecognizedRequest, ResponseCachedRequest, ValidateRequest}
import entity.response.Response
import model.CacheUnit
import org.apache.http.HttpHeaders
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

/**
  * Created by linsixin on 2017/9/14.
  * Check whether the request can be put
  * into cache. If it can then put it into
  * cache and wait for fulfill.
  */
class CacheHandler(val httpCache: HttpCache) {

  private val logger = LoggerFactory.getLogger(getClass)

  def handle(request:HeaderRecognizedRequest): HeaderRecognizedRequest ={
    val absUri = getAbsoluteUri(request)
    httpCache.get(absUri) match {
      case Some(cacheUnit) =>
        decideIfRequestShouldValidateEveryTime(absUri,request,cacheUnit)
      case None =>
        decideCacheTypeAndPutCacheUnit(absUri,request)
    }

  }


  private def getAbsoluteUri(request:HeaderRecognizedRequest):String = {
    val uri = request.uri
    if(uri.startsWith("http"))
      uri
    else{
      request.getHost match {
        case None =>
          uri
        case Some(host) =>
          s"$host$uri"
      }
    }
  }

  private def decideIfRequestShouldValidateEveryTime(absUri:String,
                                                     request:HeaderRecognizedRequest,
                                                     cacheUnit: CacheUnit) = {
    if(cacheUnit.hasFilled) {
      if(cacheUnit.isOutOfDate){
        logger.info(s"${LoggerMark.cache} has cache but expire")
        val validateRequest =
          addIfNoneMatchAndIfModifySinceHeaders(request,cacheUnit.getResponse)
        ValidateRequest(absUri,validateRequest)
      }else{
        logger.info(s"${LoggerMark.cache} take from cache:\n + ${cacheUnit.getResponse.mkHttpString()}")
        ResponseCachedRequest(absUri, cacheUnit.getResponse)
      }
    }else{
      logger.info(s"${LoggerMark.cache} has cache unit but not fill")
      ValidateRequest(absUri,request)
    }
  }

  private def decideCacheTypeAndPutCacheUnit(absUri:String,
                                 request:HeaderRecognizedRequest) = {
    decideCacheType(request) match {
      case _ : UnCacheableRequest =>
        logger.info(s"${LoggerMark.cache} not allow to cache :\n" +
          s"${request.mkHttpStringOfFirstLineAndHeaders}")
        request
      case cacheableRequest =>
        logger.info(s"${LoggerMark.cache} no cache,create new one:\n + $cacheableRequest")
        httpCache.put(absUri,CacheUnit(absUri,cacheableRequest))
        ValidateRequest(absUri,request) // need to fulfill
    }
  }

  private def addIfNoneMatchAndIfModifySinceHeaders(request: HeaderRecognizedRequest, response: Response) = {
    logger.info(s"${LoggerMark.cache} decorate, add last-modify and etag")
    val addition = ArrayBuffer[(String,String)]()
    val eTag = response.headers.find(_._1 == HttpHeaders.ETAG).getOrElse("" -> "")._2
    addition += HttpHeaders.IF_NONE_MATCH -> eTag
    val lastModify = response.headers.find(_._1 == HttpHeaders.LAST_MODIFIED).getOrElse("" -> "")._2
    addition += HttpHeaders.IF_MODIFIED_SINCE -> lastModify
    val newHeaders = request.headers ++ addition.filter( _._2 != "")
    request.updateHeaders(newHeaders)
  }

  val cacheHeader = Array("")
  val unCacheHeader = Array("")

  private def decideCacheType(request: HeaderRecognizedRequest):Cacheable = {
    if(request.method.toUpperCase != "GET" ||
        (unCacheHeader exists request.headersContains))
      UnCacheableRequest.instance
    else {
      request.getHeaderValue(HttpHeaders.CACHE_CONTROL) match {
        case None =>
          request.getHeaderValue(HttpHeaders.EXPIRES) match {
            case None => new NotSureCacheRequest
            case Some(expire) =>
              new DirectResponseRequest(new ExpiryValidate(expire))
          }
        case Some(cacheControl) =>
          val values = cacheControl.split(",")
          if (anyEquals(values, "no-store") || anyEquals(values, "private"))
            UnCacheableRequest.instance
          else if (anyEquals(values, "no-cache")){
            new NeedValidateRequest(new MaxAgeValidate)
          }
          else new NotSureCacheRequest
      }
    }
  }

  private def anyEquals(parts:Array[String],content:String) = {
    parts.exists(_.trim == content)
  }

  def getCache(absUri:String):Option[CacheUnit] = {
    httpCache.get(absUri)
  }

}
