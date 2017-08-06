package http

/**
  * Created by sparr on 2017/8/6.
  */
object HttpRequestHeaders {
  val CONNECT_ESTABLISH_200 = "HTTP/1.0 200 Connection established"
  val HOST = "Host"
  val PROXY_CONNECTION = "Proxy-Connection"
  val USER_AGENT = "User-Agent"
  val CONNECT = "CONNECT"
  val CONNECTION = "Connection"
}

object HttpRequestMethod {
  val CONNECT = "CONNECT"
  val GET = "GET"
  val POST = "POST"
  val HEAD = "HEAD"
  val PUT = "PUT"
  val DELETE = "DELETE"
  val TRACE = "TRACE"
}
