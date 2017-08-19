package entity.body

import entity.request.Request

/**
  * Created by sparr on 2017/8/19.
  */
class BodyWrappedRequest(firstLine: String,
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
