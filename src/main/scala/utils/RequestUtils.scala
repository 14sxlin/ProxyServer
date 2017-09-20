package utils

import entity.request.HeaderRecognizedRequest
import org.apache.commons.lang3.StringUtils
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/19.
  */
object RequestUtils {

  val logger : Logger = LoggerFactory.getLogger(getClass)
  /**
    * transform nameValue pair to http able header string
    *
    * @param nameValue a name value tuple
    * @return
    */
  def header2String(nameValue: (String, String)): String = {
    s"${nameValue._1}: ${nameValue._2}"
  }

  def updateAbsoluteUriToRelative(request:HeaderRecognizedRequest) :HeaderRecognizedRequest = {
    val uri = request.uri
    if(uri.startsWith("http")){
      logger.info(s"abs uri : $uri")
      val host = request.getHost.get
      val newUri = StringUtils.substringAfter(uri,host)
      logger.info(s"new uri : $newUri")
      val newFirstLine = request.firstLine.split(" ")
      newFirstLine(1) = newUri
      logger.info(s"new first line: ${newFirstLine.mkString(" ")}")
      request.updateFirstLine(newFirstLine.mkString(" "))
    }else{
      request
    }

  }
}
