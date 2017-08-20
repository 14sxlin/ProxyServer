package entity.body

import entity.body.FormParams.postBody2Param
import entity.request.Request
import org.slf4j.LoggerFactory

/**
  * Created by sparr on 2017/8/19.
  */
object BodyEntityFactory {

  private val logger = LoggerFactory.getLogger(getClass)

  def createBodyEntity(request: Request): BodyEntity = {
    if (request == Request.EMPTY)
      throw new Exception("empty request")

    val uri = request.firstLineInfo._2
    if (uri.endsWith(":443")) {
      logger.info("detect 443 : encrypt data type")
      EncryptData(request.body)
    }
    else {
      createByContentType(request)
    }
  }

  private def createByContentType(request: Request): BodyEntity = {
    request.getHeaderValue("Content-Type") match {
      case Some(contentType) =>
        createByContentType(contentType, request.body)
      case None =>
        logger.info("no Content-Type : encrypt type body")
        EncryptData(request.body)
    }
  }

  private def createByContentType(contentType: String, body: String): BodyEntity = {
    contentType match {
      case _ if contentType.startsWith("text/") =>
        logger.info(s"$contentType => TextPlain")
        TextPlain(body)
      case _ if contentType == "application/x-www-form-urlencoded" =>
        logger.info(s"$contentType => FormParams")
        FormParams(postBody2Param(body))
      case _ =>
        logger.info(s"$contentType => EncryptData")
        EncryptData(body)

    }
  }


}
