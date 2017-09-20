package task

import entity.response.TextResponse
import org.apache.http.client.methods.HttpUriRequest
import utils.HttpUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
  * Created by linsixin on 2017/8/12.
  * Every single session will using a
  * new HttpClient using HttpUtils.
  */
class OncePostTask(request: HttpUriRequest)
                            extends Task(request) {
  override def begin(): Unit = {
    if (request == null)
      return
    val postResult = Future[TextResponse]{
      logger.info("able has been send")
      HttpUtils.execute(request)
    }

    postResult onSuccess {
      case response => onSuccess(response)
    }

    postResult onFailure {
      case t : Exception =>
        logger.error(""+t.getCause)
        onFail(t)
    }
  }

}
