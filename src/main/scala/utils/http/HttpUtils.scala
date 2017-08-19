package utils.http

import entity.Response
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.{Logger, LoggerFactory}
/**
  * Created by linsixin on 2017/8/7.
  */
object HttpUtils {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def execute(request: HttpUriRequest): Response = {
    val client = HttpClients.createDefault()
    val httpResponse = client.execute(request)

    val response = Response(
      httpResponse.getStatusLine.toString,
      httpResponse.getAllHeaders.map(h => (h.getName, h.getValue)),
      EntityUtils.toString(httpResponse.getEntity))

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
