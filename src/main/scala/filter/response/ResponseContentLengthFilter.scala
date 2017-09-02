package filter.response
import constants.LoggerMark
import entity.response.{BinaryResponse, Response, TextResponse}
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
      if(isContentLengthCorrect(response))
        response
      else{
        logger.warn(s"${LoggerMark.process} content-length not conform")
        altContentLength(response,""+response.getContentLength)
      }
    }
    else addContentLength(response)

  }

  private def isContentLengthHeader(nameValue:(String,String)) = {
    nameValue._1 == contentLength
  }
  private def isContentLengthCorrect(response: Response) = {
    val contentLength = response.headers.find(isContentLengthHeader).get._2.toInt
    contentLength == response.getContentLength
  }

  private def addContentLength(response: Response) = {
    response match {
      case r:TextResponse =>
        val newHeaders =  response.headers :+ (contentLength,s"${r.body.length}")
        TextResponse(
          r.firstLine,
          newHeaders,
          r.body
        )
      case r:BinaryResponse =>
        val newHeaders =  response.headers :+ (contentLength,s"${r.body.length}")
        BinaryResponse(
          r.firstLine,
          newHeaders,
          r.body
        )
    }
  }

  private def altContentLength(response: Response,newContentLength:String) = {
    response match {
      case r:TextResponse =>
        val newHeaders =  response.headers
        val indexOfContentLength = newHeaders.indexWhere(isContentLengthHeader)
        newHeaders.update(indexOfContentLength,(HttpHeaders.CONTENT_LENGTH,newContentLength))
        TextResponse(
          r.firstLine,
          newHeaders,
          r.body
        )
      case r:BinaryResponse =>
        val newHeaders =  response.headers
        val indexOfContentLength = newHeaders.indexWhere(isContentLengthHeader)
        newHeaders.update(indexOfContentLength,(HttpHeaders.CONTENT_LENGTH,newContentLength))
        BinaryResponse(
          r.firstLine,
          newHeaders,
          r.body
        )
    }
  }


}
