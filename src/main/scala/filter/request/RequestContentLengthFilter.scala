package filter.request

import entity.request.{Request, TotalEncryptRequest}
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/20.
  * HttpClient will help you to add content-length
  * and when there is already one, it throws exception.
  * Therefore need to remove.
  */
object RequestContentLengthFilter extends HeaderFilter {

  override protected def format(request: Request): Request = {
    Request(
      request.firstLine,
      request.headers.filter(nameValue =>
        nameValue._1 != HttpHeaders.CONTENT_LENGTH),
      request.body
    )
  }
}
