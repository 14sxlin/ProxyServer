package task

import entity.response.TextResponse
import org.apache.http.client.methods.HttpUriRequest
import utils.HttpUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by sparr on 2017/8/12.
  * Every single session will using a
  * new HttpClient using HttpUtils.
  */
class OnceGetTask(request: HttpUriRequest)
                  extends Task(request){

  override def begin(): Unit = {
    if (request == null)
      return
    val doGetResult = Future[TextResponse]{
      logger.info("able has been send")
      HttpUtils.execute(request)
    }

    doGetResult onSuccess {
      case response : TextResponse =>
        onSuccess(response)
      case _ =>
        throw new Exception("unknown response")
    }
    doGetResult onFailure {
      case e : Exception =>
        onFail(e)
      case any : AnyRef =>
        logger.error("get strange thing :" + any.toString)
    }
  }

}
