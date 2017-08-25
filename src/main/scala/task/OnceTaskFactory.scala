package task

import constants.HttpRequestMethod
import org.apache.http.client.methods.HttpUriRequest
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by sparr on 2017/8/11.
  */
object OnceTaskFactory {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  def createTask(request: HttpUriRequest): Task = {
    request.getMethod match {
      case HttpRequestMethod.GET =>
        new OnceGetTask(request)
      case HttpRequestMethod.POST =>
        new OncePostTask(request)
      //      case HttpRequestMethod.CONNECT =>
      //        logger.debug("cannot process connect")

    }
  }

}
