package task

import entity.Response
import org.apache.http.client.methods.HttpUriRequest
import utils.http.HttpUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
  * Created by linsixin on 2017/8/12.
  *
  */
class PostTask(request: HttpUriRequest)
                            extends Task(request) {
  override def begin(): Unit = {
    if (request == null)
      return
    val postResult = Future[Response]{
      logger.info("request has been send")
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
