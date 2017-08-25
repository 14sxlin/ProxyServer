package entity.request.dispatch

import org.apache.http.client.protocol.HttpClientContext

/**
  * Created by linsixin on 2017/8/25.
  * RequestQueue with HttpClientContext
  */
class RequestSession(val hash:String) extends RequestQueue{

  val context:HttpClientContext = HttpClientContext.create()

}
