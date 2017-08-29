package entity.request.dispatch

import connection.ConnectionConstants
import connection.control.ActiveControl
import entity.response.Response
import http.RequestProxy
import org.apache.http.client.methods.HttpUriRequest
import org.slf4j.LoggerFactory

import scala.util.control.Breaks._

/**
  * Created by linsixin on 2017/8/25.
  */
class RequestSessionConsumeThread(requestSession: RequestSession,
                                  proxy:RequestProxy,
                                  onSuccess: Response => Unit) extends Thread with ActiveControl {


  override protected val idleThreshold = ConnectionConstants.idleThreshold

  private val logger = LoggerFactory.getLogger(getClass)

  val onFail : Exception => Unit = (e:Exception) => {
    logger.error("",e)
  }

  override def run(): Unit = {
    try {
      breakable{
        while(true){
          updateActiveTime()
          requestSession.take() match {
            case QuitRequest =>
              logger.info("Queue consumer shutdown")
              break()
            case request: HttpUriRequest =>
              val response = proxy.doRequest(request,
                requestSession.context)
              onSuccess(response)
          }
        }
      }

    }catch{
      case e:Exception =>
        onFail(e)
    }
  }


  def shutdown() :Unit = {
    requestSession.put(QuitRequest)
  }

  override def timeToClose(): Unit = {
    shutdown()
  }

}
