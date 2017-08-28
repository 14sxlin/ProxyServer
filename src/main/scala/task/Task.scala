package task

import entity.response.TextResponse
import org.apache.http.client.methods.HttpUriRequest
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/11.
  */
abstract class Task(request: HttpUriRequest) {
  protected val logger: Logger = LoggerFactory.getLogger(getClass)

  var onSuccess: (TextResponse) => Unit =
    (_) => throw new Exception("task should assign onSuccess")
  var onFail : (Exception) => Unit = logger.error("",_)
  def begin() : Unit
}

