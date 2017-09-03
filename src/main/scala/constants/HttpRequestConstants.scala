package constants

/**
  * Created by linsixin on 2017/8/6.
  */
object HttpRequestHeaders {
  val CONNECT_ESTABLISH_200 = "HTTP/1.0 200 Connection established"
}

object HttpRequestMethod {
  val CONNECT = "CONNECT"
  val GET = "GET"
  val POST = "POST"
  val HEAD = "HEAD"
  val PUT = "PUT"
  val DELETE = "DELETE"
  val TRACE = "TRACE"
  val list = Array(CONNECT,GET,POST,HEAD,PUT,DELETE,TRACE)
}
