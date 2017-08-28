//package task
//
//import entity.request.dispatch.RequestSession
//import entity.response.TextResponse
//import http.RequestProxy
//import org.apache.http.client.methods.HttpUriRequest
//
//import scala.concurrent.Future
//
///**
//  * Created by linsixin on 2017/8/25.
//  */
//class PoolGetTask(session:RequestSession, proxy:RequestProxy) extends Task(session){
//
//  override def begin(): Unit = {
//    if (session == null)
//      return
//    val postResult = Future[TextResponse]{
//      logger.info("request has been send")
//      proxy.doRequest(session.,session.context)
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
//
