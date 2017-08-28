package entity.request

import constants.HttpRequestMethod
import exception.NotHeaderException
import org.apache.commons.lang3.StringUtils
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/20.
  * This is object due to read input stream and
  * build up a request class, which separates
  * request line,headers and body
  */
object RequestFactory {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  private val NOT_FOUND = -1

  def buildRequest(requestRawData: String): Request = {

    if (requestRawData.isEmpty) {
      logger.warn("request raw data is empty")
      return Request.EMPTY
    }
    val parts = getParts(requestRawData)
    val firstLine = parts(0)

    if(isNotCorrectHttpLine(firstLine) || parts.length == 0){
      return new TotalEncryptRequest(requestRawData)
    }

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
      val indexOfEmptyLine = parts.indexOf("")
      val headers = parts.slice(1, indexOfEmptyLine).map(parseHeaderInLine)
      val body = parts.slice(indexOfEmptyLine+1,parts.length).mkString
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
