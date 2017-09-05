package utils.http

import entity.response.{BinaryResponse, TextResponse}
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.{Logger, LoggerFactory}
/**
  * Created by linsixin on 2017/8/7.
  */
object HttpUtils {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  val establishConnectInfo : String =
    "HTTP/1.1 200 Connection Established\n" +
    "Content-Length: 0\n\n"

  val unauthenicationInfo : String =
    "HTTP/1.1 407 Unauthorized\n" +
    "Content-Length: 0\n\n"

  def execute(request: HttpUriRequest): TextResponse = {
    val client = HttpClients.createDefault()
    val httpResponse = client.execute(request)

    val entity = httpResponse.getEntity
    val response = TextResponse(
      httpResponse.getStatusLine.toString,
      httpResponse.getAllHeaders.map(h => (h.getName, h.getValue)),
      entity match {
        case null => StringUtils.EMPTY
        case _ => EntityUtils.toString(entity).trim
      }
    )

    httpResponse.close()
    client.close()
    logResponse(response)

    response

  }

  def executeReturnBinary(request: HttpUriRequest): BinaryResponse = {
    val client = HttpClients.createDefault()
    val httpResponse = client.execute(request)

    val entity = httpResponse.getEntity
    val response = BinaryResponse(
      httpResponse.getStatusLine.toString,
      httpResponse.getAllHeaders.map(h => (h.getName, h.getValue)),
      entity match {
        case null => Array.emptyByteArray
        case _ =>
          IOUtils.dataFromInputStream(entity.getContent)
      }
    )


    httpResponse.close()
    client.close()
    response
  }

  private def logResponse(response: TextResponse): Unit = {
    logger.info("status line : {}", response.firstLine)
    response.headers.foreach(h => println(s"${h._1} : ${h._2}"))
    logger.info("body content length : {}", response.body.length)
  }


}
