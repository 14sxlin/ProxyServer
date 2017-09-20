package entity.request.adapt

import entity.request._
import org.apache.http.client.methods.{HttpGet, HttpUriRequest}

/**
  * Created by linsixin on 2017/8/19.
  */
object NoneBodyRequestAdapter extends RequestAdapter {

  override def adapt(request: Request): HttpUriRequest = {
    request match {
      case EmptyRequest  =>
        throw new IllegalArgumentException("empty able")
      case _:TotalEncryptRequest =>
        throw new IllegalArgumentException("total encrypt able shouldn't process here")
      case r: EmptyBodyRequest =>
        new HttpGet(r.uri)
    }
  }

}
