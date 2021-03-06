package entity.response

/**
  * Created by linsixin on 2017/8/28.
  */
trait Response {
  var connectionCloseFlag:Boolean = false
  val firstLine:String
  val headers:Array[(String,String)]
  val body:AnyRef
  def getStatusCode :String = {
    firstLine.split(" ")(1)
  }
  def getContentLength : Int
  def mkHttpBinary(encoding:String = "utf-8"):Array[Byte]
  def mkHttpString(encoding:String = "utf-8"):String
  def getHeadersString : String
  def updateHeaders(newHeaders:Array[(String,String)]) : Response
}
