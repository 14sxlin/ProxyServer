package entity.request

import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpHeaders
import utils.RequestUtils.header2String
/**
  * Created by linsixin on 2017/9/5.
  */
abstract class HeaderRecognizedRequest(val firstLine:String,
                                       val headers:Array[(String,String)]) extends Request{


  /**
    * parse http able able first line
    *
    * @return (able-method,able-uri,http-version)
    */
  def firstLineInfo: (String, String, String) = {
    if (firstLine == null || isUnknownRequestLine(firstLine))
      throw new IllegalArgumentException(s"$firstLine is not a http able line")
    val partsOfLine = firstLine.trim.split(" ")
    val method = partsOfLine(0)
    val uri = partsOfLine(1)
    val version = partsOfLine(2)
    (method, uri, version)
  }

  private def isUnknownRequestLine(requestLine: String) = {
    if (requestLine == null) {
      true
    } else {
      val firstBlankIndex = requestLine.indexOf(" ")
      val lastBlankIndex = requestLine.lastIndexOf(" ")
      firstBlankIndex == -1 ||
        lastBlankIndex == -1 ||
        requestLine.split(" ").length != 3
    }
  }

  /**
    * format to http able format with empty line
    * using \r\n to separate rather than \n
    */
  def mkHttpStringOfFirstLineAndHeaders: String = {
    val str = new StringBuilder
    str.append(s"$firstLine\r\n")
      .append(headers.map(header2String).mkString("\r\n"))
      .append("\r\n" * 2)
    str.toString()
  }

  def updateFirstLine(newFirstLine:String):HeaderRecognizedRequest


  def updateHeaders(newHeaders:Array[(String,String)]):HeaderRecognizedRequest

  def method : String = {
    firstLineInfo._1
  }

  def uri : String = {
    firstLineInfo._2
  }

  def version : String = {
    firstLineInfo._3
  }

  def headersContains(headerName: String): Boolean = {
    if (headers.isEmpty)
      false
    else headers.exists(_._1 == headerName)
  }

  def getHeaderValue(headerName: String): Option[String] = {
    headers.find(_._1 == headerName) match {
      case Some((_, value)) =>
        Some(value.trim)
      case None =>
        None
    }
  }

  def getContentEncoding :Option[String] = {
    getHeaderValue(HttpHeaders.CONTENT_TYPE) match {
      case Some(contentType) =>
        val encode = StringUtils.substringAfter(contentType,"charset=")
        if(encode.isEmpty)
          None
        else Some(encode)
      case None =>
        None
    }

  }

  def getHost : Option[String] = {
    getHeaderValue(HttpHeaders.HOST)
  }
}
