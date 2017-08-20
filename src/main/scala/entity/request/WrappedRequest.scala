package entity.request

import entity.body.BodyEntity

/**
  * Created by sparr on 2017/8/19.
  */
class WrappedRequest(firstLine: String,
                     headers: Array[(String, String)],
                     body: String,
                     val bodyEntity: BodyEntity)
  extends Request(firstLine, headers, body) {
  def this(request: Request,
           bodyEntity: BodyEntity) {
    this(request.firstLine, request.headers, request.body, bodyEntity)
  }

  def rawBody: String = {
    body
  }

}
