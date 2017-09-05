package entity.request
import utils.RequestUtils.header2String
/**
  * Created by linsixin on 2017/9/5.
  */
case class TextRequest(override val firstLine: String,
                       override val headers: Array[(String, String)],
                       body: String)
  extends HeaderRecognizedRequest(firstLine,headers){

  def mkHttpString() : String = {
    val str = new StringBuilder
    str.append(s"$firstLine\r\n")
      .append(headers.map(header2String).mkString("\r\n"))
      .append("\r\n" * 2)
      .append(body).toString()
  }
  override def mkHttpBinary(): Array[Byte] = {
    mkHttpString().getBytes()
  }

  override def updateFirstLine(newFirstLine: String):HeaderRecognizedRequest = {
    TextRequest(newFirstLine,headers,body)
  }

  override def updateHeaders(newHeaders: Array[(String, String)]):HeaderRecognizedRequest = {
    TextRequest(firstLine,newHeaders,body)
  }
}
