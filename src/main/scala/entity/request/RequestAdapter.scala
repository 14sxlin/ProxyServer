package entity.request

import org.apache.http.client.methods.HttpUriRequest

/**
  * Created by linsixin on 2017/8/20.<br/>
  * Change Request or WrapRequest to HttpRequest
  * that HttpClient used to send Http request to
  * server later.
  *
  * If request is not WrappedRequest , then its
  * entity will be regard as ByteArrayEntity
  */
trait RequestAdapter {

  def adapt(request: Request): HttpUriRequest

  def putHeaders(request: Request, httpRequest: HttpUriRequest): Unit = {
    for ((name, value) <- request.headers) {
      httpRequest.addHeader(name, value)
    }
  }

}
