package entity.request

import org.apache.http.client.methods.{HttpGet, HttpUriRequest}

/**
  * Created by linsixin on 2017/8/19.
  */
object GetRequestAdapter extends RequestAdapter {

  override def adapt(request: Request): HttpUriRequest = {
    val uri = request.firstLineInfo._2
    val httpGet = new HttpGet(uri)
    putHeaders(request, httpGet)
    httpGet
  }

}
