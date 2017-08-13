package task

import entity.{Request, Response}
import utils.http.{HttpUtils, RequestUtils}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
  * Created by linsixin on 2017/8/12.
  *
  */
class PostTask(request: Request)
                            extends Task(request) {
  override def begin(): Unit = {
    if(request == Request.EMPTY)
      return
    val postResult = Future[Response]{
      RequestUtils.doPostByHttpClient(request,
        HttpUtils.postBody2Param(request.body))
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
