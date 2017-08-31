package filter.response
import entity.response.{BinaryResponse, Response, TextResponse}
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/27.
  * Add Content-Length header to response
  */
object ResponseContentLengthFilter extends ResponseFilter{

  private val contentLength = HttpHeaders.CONTENT_LENGTH
  override def handle(response: Response): Response = {
    if(response.headers.exists(nameValue => nameValue._1 == contentLength))
      response
    else{
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

  }


}
