package filter.request

import entity.request._

/**
  * Created by linsixin on 2017/8/11.
  */
trait RequestFilter {

  def handle(request: HeaderRecognizedRequest): HeaderRecognizedRequest = {
    request match {
      case null => throw new IllegalArgumentException("request should not be null")
      case request : HeaderRecognizedRequest =>
        format(request)
    }
  }

  /**
    * @param request request which is not null or empty request
    *                or TotalEncryptRequest
    * @return new request after processing
    */
  protected def format(request: HeaderRecognizedRequest):HeaderRecognizedRequest


}