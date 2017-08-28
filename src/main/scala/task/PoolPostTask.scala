//package task
//
//import entity.response.Response
//import http.RequestProxy
//import org.apache.http.client.methods.HttpUriRequest
//import utils.http.HttpUtils
//
//import scala.concurrent.Future
//
///**
//  * Created by sparr on 2017/8/25.
//  */
//class PoolPostTask(request:HttpUriRequest,proxy:RequestProxy) extends Task(request){
//
//  override def begin(): Unit = {
//    if (request == null)
//      return
//    val postResult = Future[Response]{
//      logger.info("session has been send")
//      proxy.doRequest(request)
//    }
//
//    postResult onSuccess {
//      case response => onSuccess(response)
//    }
//
//    postResult onFailure {
//      case t : Exception =>
//        logger.error(""+t.getCause)
//        onFail(t)
//    }
//  }
//}
