package filter.header

import entity.request.Request

/**
  * Created by linsixin on 2017/8/11.
  * when pass request or response , a proxy should not<br/>
  * pass any thing about connection alive, so this handler <br/>
  * helps to get rid of related headers from request or response
  */
class ProxyHeaderFilter extends HeaderFilter {

  override def handle(request: Request): Request = {
    val dropHeader = request.headers.find(nameValue => {
      val header = nameValue._1
      header == "Connection"
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
