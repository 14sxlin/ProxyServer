package filter.request

import entity.request.HeaderRecognizedRequest
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/11.
  * when pass request or response , a proxy should not
  * pass any thing about connection alive, so this handle
  * helps to get rid of related headers from request or response
  */
object ProxyHeaderFilter extends RequestFilter {

  override protected def format(request: HeaderRecognizedRequest): HeaderRecognizedRequest = {
    def isConnectionHeader(nameValue:(String,String)) = nameValue._1 == HttpHeaders.CONNECTION
    val needDropHeaders = request.headers find isConnectionHeader match {
      case Some(nameValue) =>
        val value = nameValue._2
        value.trim.split(",")
      case _ => Array.empty[String]
    }
    val newHeaders = request.headers.filterNot(nameValue => {
      val name = nameValue._1
      name.contains("Proxy") ||
        name.contains("Connection") ||
        name.contains("Keep-Alive") ||
        needDropHeaders.contains(name)
    })
    request.updateHeaders(newHeaders)
  }
}
