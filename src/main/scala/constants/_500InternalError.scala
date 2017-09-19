package constants

/**
  * Created by linsixin on 2017/9/19.
  */
object _500InternalError {

  def content(msg:String,body:String) =
    s"""HTTP/1.1 500 $msg
       |Content-Type: text/html; charset=utf8
       |Connection: keep-alive
       |Cache-Control: must-revalidate,no-cache,no-store
       |
       |$body"""

}
