package entity.request.dispatch

import entity.response.Response
import http.RequestProxy
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/25.
  */
class RequestSessionConsumeThread(requestSession: RequestSession,
                                  proxy:RequestProxy,
                                  onSuccess:Response => Unit) extends Thread{

  private val logger = LoggerFactory.getLogger(getClass)

  @volatile private var isShutDown = false


  val onFail : Exception => Unit = (e:Exception) => {
    logger.error("",e)
  }
  override def run(): Unit = {
    try {
      while(!isShutDown){
        val response = proxy.doRequest(requestSession.take(),
          requestSession.context)
        onSuccess(response)
      }
    }catch{
      case e:Exception =>
        onFail(e)
    }
  }


  def shutdown() :Unit = {
    isShutDown = true
  }

}
