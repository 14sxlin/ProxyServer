package filter.request

import entity.request._

/**
  * Created by linsixin on 2017/8/11.
  */
trait RequestFilter {

  def handle(request: HeaderRecognizedRequest): HeaderRecognizedRequest = {
    request match {
      case null => throw new IllegalArgumentException("able should not be null")
      case request : HeaderRecognizedRequest =>
        format(request)
    }
  }

  /**
    * @param request able which is not null or empty able
    *                or TotalEncryptRequest
    * @return new able after processing
    */
  protected def format(request: HeaderRecognizedRequest):HeaderRecognizedRequest


}