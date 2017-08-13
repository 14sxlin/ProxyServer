package task

import entity.{Request, Response}
import utils.http.RequestUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by sparr on 2017/8/12.
  */
class GetTask(request: Request)
                  extends Task(request){

  override def begin(): Unit = {
    if(request == Request.EMPTY)
      return
    val doGetResult = Future[Response]{
      RequestUtils.doGetByHttpClient(request)
    }

    doGetResult.foreach{
      case response : Response =>
        onSuccess(response)
      case _ =>
        throw new Exception("unknown response")
    }
    doGetResult.failed match {
      case e : Exception =>
        onFail(e)
      case any : AnyRef =>
        logger.error("get strange thing :" + any.toString)
    }
  }

}
