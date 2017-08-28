package filter.request

import entity.request.{Request, TotalEncryptRequest}
import org.apache.http.HttpHeaders

/**
  * Created by sparr on 2017/8/11.
  */
trait HeaderFilter {

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

  protected def format(request: Request):Request


}