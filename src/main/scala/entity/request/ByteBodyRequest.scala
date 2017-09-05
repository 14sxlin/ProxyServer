package entity.request
/**
  * Created by linsixin on 2017/8/12.
  * This class represents to the request
  * that can recognize the first line
  * and headers but body
  */
case class ByteBodyRequest(override val firstLine: String,
                           override val headers: Array[(String, String)],
                           body: Array[Byte]) extends HeaderRecognizedRequest(firstLine,headers) {

  def this(request: ByteBodyRequest) {
    this(request.firstLine, request.headers, request.body)
  }

  override def mkHttpBinary() : Array[Byte] = {
    mkHttpStringOfFirstLineAndHeaders.getBytes() ++
      body
  }

  def toTextRequest(charset:String="utf8") : TextRequest = {
    TextRequest(firstLine,headers,new String(body,charset))
  }

  override def updateFirstLine(newFirstLine: String):HeaderRecognizedRequest = {
    ByteBodyRequest(newFirstLine,headers,body)
  }

  override def updateHeaders(newHeaders: Array[(String, String)]):HeaderRecognizedRequest = {
    ByteBodyRequest(firstLine,newHeaders,body)
  }
}
