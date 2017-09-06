package entity.response

import utils.RequestUtils.header2String

/**
  * Created by linsixin on 2017/8/12.
  */
case class TextResponse(firstLine:String,
                        headers: Array[(String, String)],
                        body:String) extends Response{

  override def mkHttpBinary(encoding: String = "utf-8"): Array[Byte] = {
    mkHttpString().getBytes(encoding)
  }


  /**
    * format to http response format
    *
    * @return
    */
  override def mkHttpString(encoding:String = "utf-8"): String = {
    val str = new StringBuilder
    str.append(s"$firstLine\r\n")
      .append(headers.map(header2String).mkString("\r\n"))
      .append("\r\n" * 2)
      .append(s"$body").toString().trim
  }

  override def getContentLength :Int = body.length
}
