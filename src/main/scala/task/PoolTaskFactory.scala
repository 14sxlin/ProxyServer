//package task
//
//import constants.HttpRequestMethod
//import http.RequestProxy
//import org.apache.http.client.methods.HttpUriRequest
//import org.slf4j.{Logger, LoggerFactory}
//
///**
//  * Created by linsixin on 2017/8/25.
//  */
//object PoolTaskFactory {
//  val logger: Logger = LoggerFactory.getLogger(getClass)
//
//  def createTask(request: HttpUriRequest,proxy:RequestProxy): Task = {
//    request.getMethod match {
//      case HttpRequestMethod.GET =>
//        new PoolGetTask(request,proxy)
//      case HttpRequestMethod.POST =>
//        new PoolPostTask(request,proxy)
//    }
//  }
//}
