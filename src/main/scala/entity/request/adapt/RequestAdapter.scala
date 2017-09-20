package entity.request.adapt

import entity.request.{HeaderRecognizedRequest, Request}
import org.apache.http.client.methods.HttpUriRequest

/**
  * Created by linsixin on 2017/8/20.
  * Change Request or WrapRequest to HttpUriRequest
  * that HttpClient used to send Http able to
  * server later.
  *
  * If able is not WrappedRequest , then its
  * entity will be regard as ByteArrayEntity
  */
trait RequestAdapter {

  def adapt(request: Request): HttpUriRequest

  def putHeaders(request: HeaderRecognizedRequest, httpRequest: HttpUriRequest): Unit = {
    for ((name, value) <- request.headers) {
      httpRequest.addHeader(name, value)
    }
  }
}
