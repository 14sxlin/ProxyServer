package entity.request.wrapped

import entity.body.BodyEntityFactory
import entity.request.{Request, TotalEncryptRequest}

/**
  * Created by sparr on 2017/8/20.<br/>
  * Wrap a request to WrappedRequest<br/>
  * Comparing to request, wrapped request's body part<br/>
  * is a class that extends BodyEntity
  */
object RequestWrapper {

  def wrap(request: Request) : WrappedRequest = {
    request match {
      case totalEncrytRequest :TotalEncryptRequest =>
        new MessRequest(totalEncrytRequest)
      case _ =>
        val bodyEntity = BodyEntityFactory.createBodyEntity(request)
        new WrappedRequest(request,bodyEntity)
    }
  }
}
