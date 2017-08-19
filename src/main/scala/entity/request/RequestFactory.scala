package entity.request

import constants.HttpRequestMethod
import exception.NotHeaderException
import org.apache.commons.lang3.StringUtils
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/12.
  * This is object due to read input stream and <br/>
  * build up a request class
  */
object RequestFactory {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  private val NOT_FOUND = -1

  def buildRequest(requestRawData: String): Request = {
    if (requestRawData == null) {
      logger.warn("request raw data is null")
      return Request.EMPTY
    }

    val parts = getParts(requestRawData)
    val firstLine = parts(0)

    parts.foreach(p => logger.info(s"<part : $p>"))

    if(isNotCorrectHttpLine(firstLine)){
      logger.warn(s"$firstLine is not correct begin")
      return Request.EMPTY
    }
    if (parts.length == 0) {
      logger.warn(s"request raw data is informal: $requestRawData")
      return Request.EMPTY
    }


    logger.info(firstLine)

    if(hasBodyPart(parts)){
      logger.info("<has body/>")
      buildRequestWithBody(firstLine,parts)
    }else{
      logger.info("<no body/>")
      buildRequestNoBody(firstLine,parts)
    }
  }

  private def getParts(requestRawData: String): Array[String] = {
    requestRawData.trim.split("\n").map(_.trim)
  }
  def isNotCorrectHttpLine(firstLine:String): Boolean = {
    if(firstLine == null){
      logger.info("first line is null")
      return true
    }
    val parts = firstLine.split(" ")
    parts.length != 3 ||
      !HttpRequestMethod.list.contains(parts(0).trim)
  }

  private def buildRequestWithBody(firstLine:String,parts:Array[String]) = {
    if (parts.length > 2) {
      val headers = parts.slice(1, parts.length - 2).map(parseHeaderInLine)
      val body = parts.last
      Request(firstLine, headers, body)
    } else {
      assert(parts.length == 2, "no header with body part length should be 2")
      Request(firstLine, Array.empty, parts.last)
    }
  }

  private def buildRequestNoBody(firstLine:String,parts:Array[String]) = {
    if (parts.length >= 2) {
      val headers = parts.slice(1, parts.length).map(parseHeaderInLine)
      Request(firstLine, headers, StringUtils.EMPTY)
    } else Request(firstLine, Array.empty, StringUtils.EMPTY)
  }

  private def parseHeaderInLine(line : String): (String,String) ={
    val index = line.indexOf(":")
    if(index == NOT_FOUND){
      throw new NotHeaderException(line)
    }
    val name = line.substring(0,index).trim
    val value = line.substring(index+1).trim
    (name,value)
  }

  private def hasBodyPart(requestParts:Array[String]) = {
    requestParts.contains("") &&
      requestParts.indexOf("") != requestParts.length - 1
  }


}