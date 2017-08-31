package filter.request

import entity.request.{Request, TotalEncryptRequest}
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/11.
  */
trait RequestFilter {

  def handle(request: Request): Request = {
    request match {
      case null => Request.EMPTY
      case Request.EMPTY => request
      case _ : TotalEncryptRequest =>
        request
      case _ =>
        format(request)
    }
  }

  /**
    * @param request request which is not null or empty request
    *                or TotalEncryptRequest
    * @return new request after processing
    */
  protected def format(request: Request):Request


}