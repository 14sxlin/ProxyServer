package task

import entity.{Request, Response}
import utils.http.HttpUtils

/**
  * Created by sparr on 2017/8/12.
  */
class PostTask(request: Request)
                            extends Task(request) {

  override var onSuccess: (Response) => {} = {
    (response) => {
      ???
    }
  }

  override def begin(): Unit = {
//    val response = HttpUtils.doPost(uri)
    ???
  }
}
