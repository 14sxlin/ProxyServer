package entity.request

import entity.request.EntityUtils.header2String
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/12.
  */
case class Request(firstLine: String,
                   headers: Array[(String, String)],
                   body: String) {

  def this(request: Request) {
    this(request.firstLine, request.headers, request.body)
  }


  /**
    * format to http request format
    *
    * @return
    */
  def mkHttpString: String = {
    val str = new StringBuilder
    str.append(s"$firstLine\r\n")
      .append(headers.map(header2String).mkString("\r\n"))
      .append("\r\n" * 2)
      .append(s"$body").toString()
  }

  /**
    * parse http request request first line
    *
    * @return (request-method,request-uri,http-version)
    */
  def firstLineInfo: (String, String, String) = {
    if (firstLine == null || isUnknownRequestLine(firstLine))
      throw new IllegalArgumentException(s"$firstLine is not a http request line")
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

object Request {
  val EMPTY = Request("GET ## ##", null, null)

}

