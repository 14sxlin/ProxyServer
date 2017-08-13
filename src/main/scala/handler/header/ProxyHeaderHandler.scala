package handler.header

/**
  * Created by linsixin on 2017/8/11.
  * when pass request or response , a proxy should not<br/>
  * pass any thing about connection alive, so this handler <br/>
  * helps to get rid of related headers from request or response
  */
class ProxyHeaderHandler extends HeaderHandler{

  override def handle(headers: Array[(String, String)]):
                                    Array[(String,String)] = {
    val dropHeader = headers.find( nameValue =>{
      val header = nameValue._1
      header == "Connection"
    }) match {
      case Some(nameValue) =>
        val value = nameValue._2
        value.trim.split(",")
      case _ => Array.empty[String]
    }
    val newHeaders = headers.filterNot(nameValue =>{
      val name = nameValue._1
      name.contains("Proxy") ||
        name.contains("Connection") ||
          dropHeader.contains(name)
    })
    nextHandler match {
      case null =>
        newHeaders
      case _  =>
        nextHandler.handle(newHeaders)

    }

  }
}
