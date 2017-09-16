package cache

import entity.request.{HeaderRecognizedRequest, ResponseCachedRequest, ValidateRequest}
import org.apache.http.HttpHeaders
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/9/14.
  */
class CacheHandler(httpCache: HttpCache) {

  private val logger = LoggerFactory.getLogger(getClass)

  def handle(request:HeaderRecognizedRequest): HeaderRecognizedRequest ={
    val absUri = getAbsoluteUri(request)
    httpCache.get(absUri) match {
      case Some(cacheUnit) =>
        if(cacheUnit.hasFilled)
          ResponseCachedRequest(absUri,cacheUnit.getResponse)
        else{
          ValidateRequest(absUri,request)
        }
      case None =>
        genCacheRequest(request) match {
          case None =>
            request
          case Some(cacheableRequest) =>
            httpCache.put(absUri,CacheUnit(absUri,cacheableRequest))
            ValidateRequest(absUri,request) // need to fulfill
        }
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

  val cacheHeader = Array("")
  val unCacheHeader = Array("")
  private def genCacheRequest(request: HeaderRecognizedRequest):Option[Cacheable] = {
    if(request.method.toUpperCase != "GET")
      return None
    if(unCacheHeader exists request.headersContains)
      None
    else {
      request.getHeaderValue(HttpHeaders.CACHE_CONTROL) match {
        case None => request.getHeaderValue(HttpHeaders.EXPIRES) match {
          case None => None
          case Some(expire) =>
            Some(new DirectRequest(new ExpiryValidate(expire)))
        }
        case Some(cacheControl) =>
          val values = cacheControl.split(",")
          if (anyEquals(values, "no-store") || anyEquals(values, "private"))
            None
          else if (anyEquals(values, "no-cache")){
            Some(new NeedValidateRequest(new ETagValidate))
          }
          else None
      }
    }
  }
//
//  val validateHeader = Array(
//    HttpHeaders.LAST_MODIFIED,
//    HttpHeaders.ETAG
//  )
//  private def genValidate(request:HeaderRecognizedRequest,header:String) : Validate = {
//    request.getHeaderValue(header) match{
//      case None =>
//        throw new Exception(s"no $header value find")
//      case Some(value) =>
//        if(HttpHeaders.LAST_MODIFIED == header)
//          LastModifyValidate(value)
//        else if(HttpHeaders.ETAG == header)
//          ETagValidate(value)
//        else throw new Exception(s"no matche validate header : $header")
//    }
//  }

  private def anyEquals(parts:Array[String],content:String) = {
    parts.exists(_.trim == content)
  }

}
