package task

import org.apache.commons.lang3.StringUtils

/**
  * Created by sparr on 2017/8/11.
  */
object TaskFactory {
  def createTask(methodUriVersion:(String,String,String),
                 headers:String,
                 body:String) : Task = {
    val method = methodUriVersion._1
    val uri = methodUriVersion._2
    val version = methodUriVersion._3
    StringUtils.upperCase(method) match {
      case "GET" =>
        new GetTask(method,uri,version)
      case "POST" =>
        new PostTask(method,uri,version)
    }
  }

}
