package entity.request

import entity.body.{BodyEntityFactory, EmptyBody, EncryptData}

/**
  * Created by sparr on 2017/8/20.<br/>
  * Wrap a request to WrappedRequest<br/>
  * Comparing to request, wrapped request's body part<br/>
  * is a class that extends BodyEntity
  */
object RequestWrapper {

  def wrap(request: Request) : WrappedRequest = {
    val bodyEntity = BodyEntityFactory.createBodyEntity(request)
    new WrappedRequest(request,bodyEntity)
  }
}
