package filter.request

import entity.request.HeaderRecognizedRequest
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/20.
  * HttpClient will help you to add content-length
  * and when there is already one, it throws exception.
  * Therefore need to remove.
  */
object RequestContentLengthFilter extends RequestFilter {

  override protected def format(request: HeaderRecognizedRequest): HeaderRecognizedRequest = {
    request.updateHeaders(
      request.headers.filter{nameValue =>
        nameValue._1 != HttpHeaders.CONTENT_LENGTH
      })
  }
}
