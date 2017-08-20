package filter.header

import entity.request.Request
import org.apache.http.HttpHeaders

/**
  * Created by sparr on 2017/8/11.
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
