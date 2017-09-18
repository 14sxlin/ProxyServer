package http

import java.io.ByteArrayOutputStream

import constants.LoggerMark
import entity.response.{BinaryResponse, Response}
import org.apache.http.client.entity.GzipCompressingEntity
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.{HttpHeaders, HttpResponse}
import org.slf4j.{Logger, LoggerFactory}
import utils.{GZipUtils, IOUtils}

/**
  * Created by linsixin on 2017/9/9.
  */
class CompressConnectionPoolClient extends InterceptConnectionPoolClient{

  private val logger:Logger = LoggerFactory.getLogger(getClass)

  private def compressResponse(httpResponse: HttpResponse,
                               context:HttpClientContext) : Unit = {

    val contentType = httpResponse.getAllHeaders.find(
      _.getName == HttpHeaders.CONTENT_TYPE)
    if(contentType.isEmpty){
      logger.warn(s"Content-Type of ${httpResponse.getStatusLine} not found")
      return
    }
    if(contentType.get.getValue.startsWith("text/") &&
        context.getRequestConfig.isContentCompressionEnabled){
      val in = httpResponse.getEntity.getContent
      val content = IOUtils.dataFromResponseInputStream(in)
      if(content.length >  (2 >> 10)) // 1kB
      {
        httpResponse.addHeader("Content-Encoding", "gzip")
        updateContentLength(httpResponse,content.length)
        val compress = GZipUtils.encode(content)
        val compressBytes = new ByteArrayEntity(compress)
        httpResponse.setEntity(compressBytes)
        assert(httpResponse.getAllHeaders.exists(_.getName == HttpHeaders.CONTENT_ENCODING))
      }
      in.close()
    }else{
      logger.info("not text type,don't compress")
    }

  }

  private def updateContentLength(httpResponse: HttpResponse,length:Int) : Unit = {
    val existsHeaders = httpResponse.getHeaders(HttpHeaders.CONTENT_LENGTH)
    if(existsHeaders.isEmpty)
      httpResponse.addHeader(HttpHeaders.CONTENT_LENGTH,""+length)
    else{
      httpResponse.setHeader(HttpHeaders.CONTENT_LENGTH,""+length)
      logger.info(s"${LoggerMark.process} content-length: ${httpResponse.getHeaders(HttpHeaders.CONTENT_LENGTH)(0)} : $length")
    }
  }

  override protected def adapt(httpResponse: HttpResponse): Response = {
    val entity = httpResponse.getEntity
    val headers = httpResponse.getAllHeaders.map(h => (h.getName, h.getValue))

    BinaryResponse(
      httpResponse.getStatusLine.toString,
      headers,
      entity match {
        case null => Array.emptyByteArray
        case compress: GzipCompressingEntity =>
          val out = new ByteArrayOutputStream()
          compress.writeTo(out)
          val content = out.toByteArray
          out.close()
          content
        case _ =>
          IOUtils.dataFromResponseInputStream(entity.getContent)
      }
    )
  }

  override protected def responseIntercept(response: HttpResponse, context: HttpClientContext): HttpResponse = {
    val tempResponse = super.responseIntercept(response, context)
    compressResponse(tempResponse,context)
    tempResponse
  }

}
