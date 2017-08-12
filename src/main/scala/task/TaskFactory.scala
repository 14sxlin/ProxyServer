package task

import constants.HttpRequestMethod
import entity.Request
import org.apache.commons.lang3.StringUtils

/**
  * Created by sparr on 2017/8/11.
  */
object TaskFactory {
  def createTask(request:Request) : Task = {
    request.method match {
      case HttpRequestMethod.GET =>
        new GetTask(request)
      case HttpRequestMethod.GET =>
        new PostTask(request)
    }
  }

}
