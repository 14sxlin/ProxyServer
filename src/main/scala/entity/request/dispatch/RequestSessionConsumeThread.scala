package entity.request.dispatch

import connection.CloseWhenNotActive
import entity.response.Response
import http.RequestProxy
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/25.
  */
class RequestSessionConsumeThread(requestSession: RequestSession,
                                  proxy:RequestProxy,
                                  onSuccess: Response => Unit) extends Thread with CloseWhenNotActive {


  override protected val name = s"request session @ ${requestSession.hash}"
  private val logger = LoggerFactory.getLogger(getClass)

  @volatile private var isShutDown = false


  val onFail : Exception => Unit = (e:Exception) => {
    logger.error("",e)
  }
  override def run(): Unit = {
    try {
      closeWhenNotActiveIn(10000L)
      while(!isShutDown){
        val response = proxy.doRequest(requestSession.take(),
          requestSession.context)
        updateActiveTime()
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

  override def timeToClose(): Unit = {
    shutdown()
  }

}
