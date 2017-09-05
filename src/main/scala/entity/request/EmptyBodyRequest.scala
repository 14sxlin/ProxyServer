package entity.request

/**
  * Created by linsixin on 2017/9/5.
  */
case class EmptyBodyRequest(override val firstLine:String,
                            override val headers:Array[(String,String)])
  extends HeaderRecognizedRequest(firstLine,headers){

  override def mkHttpBinary() : Array[Byte] = {
    mkHttpStringOfFirstLineAndHeaders.getBytes()
  }

  def toTextRequest(body:String) : TextRequest = {
    assert(body!=null && !body.isEmpty)
    TextRequest(firstLine,headers,body)
  }

  def toByteBodyRequest(body:Array[Byte]):ByteBodyRequest = {
    assert(body!=null && !body.isEmpty)
    ByteBodyRequest(firstLine,headers,body)
  }

  override def updateFirstLine(newFirstLine: String):HeaderRecognizedRequest = {
    EmptyBodyRequest(newFirstLine,headers)
  }

  override def updateHeaders(newHeaders: Array[(String, String)]):HeaderRecognizedRequest  = {
    EmptyBodyRequest(firstLine,newHeaders)
  }
}
