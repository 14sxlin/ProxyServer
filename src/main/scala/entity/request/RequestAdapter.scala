package entity.request

import org.apache.http.client.methods.HttpUriRequest

/**
  * Created by sparr on 2017/8/19.
  */
trait RequestAdapter {

  def adapt(request: Request): HttpUriRequest

  def putHeaders(request: Request, httpRequest: HttpUriRequest): Unit = {
    for ((name, value) <- request.headers) {
      httpRequest.addHeader(name, value)
    }
  }

}
