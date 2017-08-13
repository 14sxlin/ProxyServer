package utils.http

import constants.HttpRequestMethod
import entity.Request
import exception.NotHeaderException
import handler.header.HeaderHandler
import org.apache.commons.lang3.StringUtils
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/12.
  * This is object due to read input stream and <br/>
  * build up a request class
  */
object RequestBuilder {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  private val NOT_FOUND = -1

  def buildRequest(requestRawData:String,
                   requestHeaderHandler:HeaderHandler) : Request = {
    val parts = requestRawData.split("\r\n")
    val firstLine = parts(0)

    if(isNotCorrectHttpLine(firstLine)){
      logger.warn(s"$firstLine is not correct begin")
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
    val headers = parts.slice(1,parts.length-2).map(parseHeaderInLine)
    val body = parts.last
    Request(firstLine,headers,body)
  }

  private def buildRequestNoBody(firstLine:String,parts:Array[String]) = {
    val headers = parts.slice(1,parts.length).map(parseHeaderInLine)
    Request(firstLine,headers,StringUtils.EMPTY)
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
    requestParts.indexOf("") != requestParts.length-1
  }


}
