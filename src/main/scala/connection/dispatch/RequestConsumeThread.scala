package connection.dispatch

import java.util.concurrent.ArrayBlockingQueue

import connection.control.ClientServicePool
import entity.request.RequestUnit
import http.RequestProxy
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/25.
  */
class RequestConsumeThread(conPool:ClientServicePool,
                           requestQueue: ArrayBlockingQueue[RequestUnit],
                           requestProxy: RequestProxy) extends Thread {

  private val logger = LoggerFactory.getLogger(getClass)

  val onFail : Exception => Unit = (e:Exception) => {
    logger.error("",e)
  }

  override def run(): Unit = {
    while(true){
      try {
        val requestUnit = requestQueue.take()
        logger.info("take 1 request,rest size :"+requestQueue.size())
        val response = requestProxy.doRequest(requestUnit.request,requestUnit.context)
        requestUnit.onSuccess(response)
        val key =requestUnit.key
        if(response.connectionCloseFlag){
          if(conPool.containsKey(key)){
            conPool.closeAndRemove(key)
          }
        }
      }catch{
        case e:Exception =>
          onFail(e)
      }
    }
  }
}
