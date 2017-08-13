package utils.http

import java.io.BufferedReader

import constants.HttpRequestMethod
import entity.Request
import exception.NotHeaderException
import handler.header.HeaderHandler
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by linsixin on 2017/8/12.
  * This is object due to read input stream and <br/>
  * build up a request class
  */
object RequestBuilder {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  private val NOT_FOUND = -1

  def buildRequest(reader:BufferedReader,
                   requestHeaderHandler:HeaderHandler) : Request = {
    val firstLine = reader.readLine()
    if(isNotCorrectBegin(firstLine)){
      logger.warn(s"$firstLine is not correct begin")
//      throw new IncorrectRequest(firstLine)
    }
    val requestHeaders = requestHeaderHandler.handle(parseHeaders(reader))
    val requestBody = parseBody(reader)
    Request(firstLine,requestHeaders,requestBody)
  }

  def isNotCorrectBegin(firstLine:String): Boolean = {
    val parts = firstLine.split(" ")
    parts.length != 2 ||
      !HttpRequestMethod.list.contains(parts(0).trim)
  }

  private def parseHeaders(reader: BufferedReader) : Array[(String,String)] = {
    logger.info("<headers>")
    var line = reader.readLine()
    val nameValues = ArrayBuffer[(String,String)]()
    while(line != ""){
      logger.info(line)
      nameValues += parseHeaderInLine(line)
      line = reader.readLine()
    }
    logger.info("</headers>")
    nameValues.toArray
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

  private def parseBody(reader: BufferedReader) : String = {
    val body = new StringBuffer()
    var line = reader.readLine()
    while(line != ""){
      body.append(line)
      line = reader.readLine()
    }
    body.toString
  }

}
