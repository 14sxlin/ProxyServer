package filter.response
import entity.response.Response
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/27.
  * Add Content-Length header to response
  */
object ResponseContentLengthFilter extends ResponseFilter{

  private val contentLength = HttpHeaders.CONTENT_LENGTH
  override def handle(response: Response): Response = {
    if(response.headers.map(nameValue => nameValue._1 )
      .contains(contentLength))
      response
    else{
      val newHeaders =  response.headers :+ (contentLength,s"${response.body.length}")
      Response(
        response.firstLine,
        newHeaders,
        response.body
      )
    }

  }


}
