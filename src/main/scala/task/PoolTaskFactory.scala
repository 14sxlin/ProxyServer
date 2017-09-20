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
//  def createTask(able: HttpUriRequest,proxy:RequestProxy): Task = {
//    able.getMethod match {
//      case HttpRequestMethod.GET =>
//        new PoolGetTask(able,proxy)
//      case HttpRequestMethod.POST =>
//        new PoolPostTask(able,proxy)
//    }
//  }
//}
