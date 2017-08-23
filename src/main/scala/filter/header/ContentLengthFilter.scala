package filter.header

import entity.request.Request
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/20.
  * HttpClient will help you to add content-length
  * and when there is already one, it throws exception.
  * Therefore need to remove.
  */
object ContentLengthFilter extends HeaderFilter {
  override def handle(request: Request): Request = {
    if(request == null)
      throw new Exception("request is null")
    val headers = request.headers
    if(headers ==null || headers.isEmpty)
      request
    else{
      Request(
        request.firstLine,
        headers.filter(nameValue =>
          nameValue._1 != HttpHeaders.CONTENT_LENGTH),
        request.body
      )
    }

  }
}
