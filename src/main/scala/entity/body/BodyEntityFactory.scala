//package entity.body
//
//import constants.LoggerMark
//import entity.body.FormParams.postBody2Param
//import entity.request._
//import org.apache.commons.lang3.StringUtils
//import org.slf4j.LoggerFactory
///**
//  * Created by linsixin on 2017/8/19.
//  */
//@Deprecated
//object BodyEntityFactory {
//
//  private val logger = LoggerFactory.getLogger(getClass)
//  def createBodyEntity(request: Request): BodyEntity = {
//    request match {
//      case EmptyRequest =>
//        throw new Exception("empty request")
//      case request:TextRequest =>
//        TextPlain(request.body)
////        if (uri.endsWith(":443")) {
////          logger.info(s"${LoggerMark.process} detect 443 : encrypt data type")
////          EncryptData(request.body)
////        }else {
////          createByContentType(request)
////        }
//      case request:ByteBodyRequest =>
//        EncryptData(request.body)
//      case request : TotalEncryptRequest =>
//        EncryptData(request.bytes)
//    }
//
//
//  }
//
//  private def createByContentType(request: HeaderRecognizedRequest): BodyEntity = {
//    if(request.body.isEmpty)
//    {
//      logger.info(s"${LoggerMark.process} EmptyBody")
//      return EmptyBody
//    }
//    request.getHeaderValue("Content-Type") match {
//      case Some(contentType) =>
//        createByContentType(contentType, request.body)
//      case None =>
//        logger.info(s"${LoggerMark.process} no Content-Type : encrypt type body")
//        EncryptData(request.body)
//    }
//  }
//
//  private def createByContentType(contentType: String, body: Array[Byte]): BodyEntity = {
//    val charset = StringUtils.substringAfter(contentType,"charset=")
//    contentType match {
//      case _ if contentType.startsWith("text/") =>
//        logger.info(s"${LoggerMark.process} $contentType => TextPlain")
//        if(charset.isEmpty){
//          logger.info(s"${LoggerMark.process}  use $charset")
//          TextPlain(new String(body,charset))
//        }else{
//          logger.info(s"${LoggerMark.process} no charset info, use 'utf8'")
//          TextPlain(new String(body,"utf8"))
//        }
//
//      case _ if contentType == "application/x-www-form-urlencoded" =>
//        logger.info(s"${LoggerMark.process} $contentType => FormParams")
//        if(charset.isEmpty)
//          EncryptData(body)
//        else FormParams(postBody2Param(new String(body,charset)))
//      case _ =>
//        logger.info(s"${LoggerMark.process} $contentType => EncryptData")
//        EncryptData(body)
//    }
//  }
//
//
//}
