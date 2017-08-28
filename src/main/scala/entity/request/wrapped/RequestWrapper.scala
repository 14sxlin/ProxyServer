package entity.request.wrapped

import entity.body.BodyEntityFactory
import entity.request.{Request, TotalEncryptRequest}

/**
  * Created by linsixin on 2017/8/20.
  * Wrap a request to WrappedRequest
  * Comparing to request, wrapped request's
  * body part is a class that extends BodyEntity
  */
object RequestWrapper {

  def wrap(request: Request) : WrappedRequest = {
    request match {
      case totalEncryptRequest :TotalEncryptRequest =>
        new MessRequest(totalEncryptRequest)
      case _ =>
        val bodyEntity = BodyEntityFactory.createBodyEntity(request)
        new WrappedRequest(request,bodyEntity)
    }
  }
}
