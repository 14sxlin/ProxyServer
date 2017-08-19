package entity.request

import org.apache.http.client.methods.{HttpGet, HttpUriRequest}

/**
  * Created by linsixin on 2017/8/19.
  */
object GetRequestAdapter extends RequestAdapter {

  override def adapt(request: Request): HttpUriRequest = {
    val httpGet = new HttpGet(request.firstLineInfo._2)
    putHeaders(request, httpGet)
    httpGet
  }

}
