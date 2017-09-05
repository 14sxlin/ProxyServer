package entity.request

import constants.HttpRequestMethod
import exception.NotHeaderException
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpHeaders
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/20.
  * This object dues to read input stream and
  * build up a request class, which separates
  * request line,headers and body
  */
object RequestFactory {

  type OptionBytes = Option[Array[Byte]]
  type OptionRequestInBytes = Option[Array[Array[Byte]]]

  val logger: Logger = LoggerFactory.getLogger(getClass)

  private val NOT_FOUND = -1
  private val LF = "\n".getBytes.head
  private val CR = "\r".getBytes.head

  def buildRequest(requestBytes: Array[Byte],
                   encoding: String = "utf8"): Request = {
    if (requestBytes == null || requestBytes.isEmpty) {
      logger.warn("request raw data is empty")
      return EmptyRequest
    }
    val (firstLinePart, headersAndBodyBytes) = getFirstLinePart(requestBytes)
    if (firstLinePart.isEmpty || headersAndBodyBytes.isEmpty)
      return TotalEncryptRequest(requestBytes)

    val (headerPart, bodyPart) = getHeaderAndBodyParts(headersAndBodyBytes.get)
    if (headerPart.isEmpty)
      return TotalEncryptRequest(requestBytes)

    if (bodyPart.isEmpty || bodyPart.get.isEmpty) { //notice this
      formEmptyBodyRequest(firstLinePart.get,headerPart.get)
    } else {
      val temp = formEmptyBodyRequest(firstLinePart.get,headerPart.get)
      temp.getContentEncoding
      temp.getHeaderValue(HttpHeaders.CONTENT_TYPE) match {
        case None =>
          temp.toByteBodyRequest(bodyPart.get)
        case Some(contentType) =>
          if(StringUtils.contains(contentType,"text/"))
          {
            val charset = StringUtils.substringAfter(contentType,"charset=")
            if(charset.isEmpty)
              temp.toTextRequest(new String(bodyPart.get))
            else
              temp.toTextRequest(new String(bodyPart.get,charset))
          }
          else
            temp.toByteBodyRequest(bodyPart.get)
      }
    }
  }

  private def formEmptyBodyRequest(firstLineBytes:Array[Byte],
                                   headersBytes:Array[Byte]) : EmptyBodyRequest= {
    val (firstLine,headers) = parse(firstLineBytes,headersBytes)
    EmptyBodyRequest(
      firstLine,
      headers
    )
  }
  private def parse(firstLine:Array[Byte],
                    headers:Array[Byte]):(String,Array[(String,String)]) = {
    val f = new String(firstLine).trim
    val h = new String(headers) split "\n" filterNot { _.trim.isEmpty } map {
      header => string2Header(header.trim)
    }
    (f,h)
  }

  private def getFirstLinePart(requestAllBytes: Array[Byte]): (OptionBytes, OptionBytes) = {
    val firstIndexOfLF = requestAllBytes.indexOf(LF)
    if (firstIndexOfLF == NOT_FOUND)
      (None, None)
    else {
      val totalLength = requestAllBytes.length
      if (firstIndexOfLF == totalLength)
        (None, None)
      else {
        val firstLine = requestAllBytes.slice(0, firstIndexOfLF+1)
        val restBytes = requestAllBytes.slice(firstIndexOfLF+1, totalLength)
        (Some(firstLine), Some(restBytes))
      }
    }
  }

  private def getHeaderAndBodyParts(headersAndBodyBytes: Array[Byte]): (OptionBytes, OptionBytes) = {
    val indexOfLF = headersAndBodyBytes.indexOf(LF)
    if (indexOfLF == NOT_FOUND) // no headers
      (None, None)
    else if (indexOfLF == headersAndBodyBytes.length) // on body part or empty line
      (Some(headersAndBodyBytes), None)
    else { // may be has body
      val totalLen = headersAndBodyBytes.length
      var indexOfLFBeforeBody = indexOfLF
      var tempIndexOfLF = indexOfLF
      var nextByte = headersAndBodyBytes(tempIndexOfLF + 1)
      def hasNextByte = tempIndexOfLF < totalLen - 1
      while (hasNextByte &&  nextByte != LF &&  nextByte != CR) {
        tempIndexOfLF = headersAndBodyBytes.indexOf(LF, tempIndexOfLF + 1)
        nextByte = headersAndBodyBytes(tempIndexOfLF + 1)
      }
      if (!hasNextByte)
        (Some(headersAndBodyBytes), None)
      else {
        if(nextByte == CR){ // using CRLF to separate
          indexOfLFBeforeBody = tempIndexOfLF + 2 //skip CRLF
          (Some(headersAndBodyBytes.slice(0,indexOfLFBeforeBody)),
            Some(headersAndBodyBytes.slice(indexOfLFBeforeBody + 1, totalLen)))
        }else // nextByte is LF means using LF to separate
        indexOfLFBeforeBody = tempIndexOfLF + 1
        (Some(headersAndBodyBytes.slice(0,indexOfLFBeforeBody)),
          Some(headersAndBodyBytes.slice(indexOfLFBeforeBody + 1, totalLen)))
      }
    }
  }

  def isNotCorrectHttpLine(firstLine: String): Boolean = {
    if (firstLine == null) {
      logger.info("first line is null")
      return true
    }
    val parts = firstLine.split(" ")
    parts.length != 3 ||
      !HttpRequestMethod.list.contains(parts(0).trim)
  }

  private def string2Header(line: String): (String, String) = {
    val index = line.indexOf(":")
    if (index == NOT_FOUND) {
      throw new NotHeaderException(line)
    }
    val name = line.substring(0, index).trim
    val value = line.substring(index + 1).trim
    (name, value)
  }
}
