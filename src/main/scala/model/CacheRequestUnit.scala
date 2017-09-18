//package model
//
//import connection.ClientConnection
//import entity.response.Response
//import filter.ResponseFilterChain
//import org.apache.http.client.methods.HttpUriRequest
//import org.apache.http.client.protocol.HttpClientContext
//
///**
//  * Created by linsixin on 2017/9/16.
//  */
//class CacheRequestUnit(key:String,
//                       request:HttpUriRequest,
//                       context:HttpClientContext,
//                       onSuccess:Response => Unit) extends RequestUnit(key,request,context){
//
//  override def onSuccess(client: ClientConnection, responseFilterChain: ResponseFilterChain.type): (Response) => Unit = {
//    super.onSuccess(client, responseFilterChain)
//  }
//
//}
