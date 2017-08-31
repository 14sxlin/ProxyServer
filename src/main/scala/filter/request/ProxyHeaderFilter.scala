package filter.request

import entity.request.Request
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/11.
  * when pass request or response , a proxy should not
  * pass any thing about connection alive, so this handle
  * helps to get rid of related headers from request or response
  */
object ProxyHeaderFilter extends HeaderFilter {

  override protected def format(request: Request): Request = {
    val dropHeader = request.headers.find(nameValue => {
      val header = nameValue._1
      header == HttpHeaders.CONNECTION
    }) match {
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
        dropHeader.contains(name)
    })
    Request(request.firstLine, newHeaders, request.body)
  }
}
