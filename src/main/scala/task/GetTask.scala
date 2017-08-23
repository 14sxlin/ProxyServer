package task

import entity.response.Response
import org.apache.http.client.methods.HttpUriRequest
import utils.http.HttpUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by sparr on 2017/8/12.
  */
class GetTask(request: HttpUriRequest)
                  extends Task(request){

  override def begin(): Unit = {
    if (request == null)
      return
    val doGetResult = Future[Response]{
      logger.info("request has been send")
      HttpUtils.execute(request)
    }

    doGetResult onSuccess {
      case response : Response =>
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
