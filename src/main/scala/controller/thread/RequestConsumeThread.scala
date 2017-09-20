package controller.thread

import java.util.concurrent.ArrayBlockingQueue

import connection.pool.ClientContextUnitPool
import http.RequestProxy
import model.RequestUnit
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/25.
  */
class RequestConsumeThread(conPool:ClientContextUnitPool,
                           requestQueue: ArrayBlockingQueue[RequestUnit],
                           requestProxy: RequestProxy) extends Thread {

  private val logger = LoggerFactory.getLogger(getClass)

  override def run(): Unit = {
    while(true){
      val requestUnit = requestQueue.take()
      try {
        val response = requestProxy.doRequest(requestUnit.request,requestUnit.context)
        requestUnit.onSuccess(response)
        val key =requestUnit.key
        if(response.connectionCloseFlag){
          if(conPool.containsKey(key)){
            conPool.closeAndRemove(key)
          }
        }
        requestProxy.closeIdleConnection(10)
      }catch{
        case e:Exception =>
          logger.error(s"${requestUnit.key} crash,reponse 500")
          requestUnit.request.getAllHeaders.foreach{
            headers =>
              logger.error(s"${headers.getName} : ${headers.getValue}")
          }
          logger.error("",e)
          requestUnit.onFail(e)
      }
    }
  }
}
