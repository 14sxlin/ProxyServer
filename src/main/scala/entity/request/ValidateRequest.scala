package entity.request

/**
  * Created by linsixin on 2017/9/15.
  * This class represent to the request
  * can cache but not fulfill yet. So when
  * finish getting response also need to
  * fulfill the cache unit found by absUri.
  */
case class ValidateRequest(absUri:String,
                           request:HeaderRecognizedRequest) extends HeaderRecognizedRequest(request.firstLine,request.headers){

  def getUri : String = request.uri

  override def updateFirstLine(newFirstLine: String): HeaderRecognizedRequest = {
    request.updateFirstLine(newFirstLine)
  }

  override def updateHeaders(newHeaders: Array[(String, String)]): HeaderRecognizedRequest = {
    request.updateHeaders(newHeaders)
  }

  override def mkHttpBinary(): Array[Byte] = request.mkHttpBinary()
}