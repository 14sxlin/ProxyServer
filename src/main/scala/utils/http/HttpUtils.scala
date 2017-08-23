package utils.http

import entity.response.Response
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

  def execute(request: HttpUriRequest): Response = {
    val client = HttpClients.createDefault()
    val httpResponse = client.execute(request)

    val entity = httpResponse.getEntity
    val response = Response(
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

  private def logResponse(response: Response) = {
    logger.info("status line : {}", response.firstLine)
    response.headers.foreach(h => println(s"${h._1} : ${h._2}"))
    logger.info("body content length : {}", response.body.length)
  }


}
