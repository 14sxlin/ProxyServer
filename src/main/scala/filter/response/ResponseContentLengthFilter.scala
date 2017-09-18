package filter.response
import constants.LoggerMark
import entity.response.Response
import org.apache.http.HttpHeaders
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/27.
  * Add Content-Length header to response
  */
object ResponseContentLengthFilter extends ResponseFilter{

  private val logger = LoggerFactory.getLogger(getClass)

  private val contentLength = HttpHeaders.CONTENT_LENGTH
  override def handle(response: Response): Response = {
    if(response.headers.exists(isContentLengthHeader))
    {
      val (isCorrect, originalContentLen) = isContentLengthCorrect(response)
      if(isCorrect || response.headers.exists( _._1 == HttpHeaders.CONTENT_ENCODING))
        response
      else {
        logger.warn(s"${LoggerMark.process} length not conform \n" +
          s"old $originalContentLen: new ${response.getContentLength}")
        logger.warn("\n" + response.mkHttpString())
        altContentLength(response, "" + response.getContentLength)
      }
    }
    else addContentLength(response)

  }

  private def isContentLengthHeader(nameValue:(String,String)) = {
    nameValue._1 == contentLength
  }
  private def isContentLengthCorrect(response: Response):(Boolean,Int) = {
    val contentLength = response.headers.find(isContentLengthHeader).get._2.toInt
    (contentLength == response.getContentLength,contentLength)
  }

  private def addContentLength(response: Response) = {
    response.body match {
      case body:String =>
        val newHeaders =  (contentLength,s"${body.length}") +: response.headers
        response.updateHeaders(newHeaders)
      case body:Array[Byte] =>
        val newHeaders =  (contentLength,s"${body.length}") +: response.headers
        response.updateHeaders(newHeaders)
    }
  }

  protected def altContentLength(response: Response,newContentLength:String): Response = {
    val newHeaders =  response.headers
    val indexOfContentLength = newHeaders.indexWhere(isContentLengthHeader)
    newHeaders.update(indexOfContentLength,(HttpHeaders.CONTENT_LENGTH,newContentLength))
    response.updateHeaders(newHeaders)
  }


}
