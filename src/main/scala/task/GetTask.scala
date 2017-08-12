package task

import entity.{Request, Response}
import utils.http.HttpUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by sparr on 2017/8/12.
  */
class GetTask(request: Request)
                  extends Task(request){

  override var onSuccess: (Response) => {} =
    (response) =>{
      ???
    }

  override def begin(): Unit = {
    val doGetResult = Future[Response]{
      HttpUtils.doGet(request)
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
    }


  }
}
