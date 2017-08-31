package entity.request

import entity.response.Response
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext

/**
  * Created by linsixin on 2017/8/30.
  */
case class RequestUnit(key:String,
                       request:HttpUriRequest,
                       context:HttpClientContext,
                       onSuccess:Response => Unit)
