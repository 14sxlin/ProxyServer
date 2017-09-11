package entity.response

import utils.RequestUtils

/**
  * Created by linsixin on 2017/8/12.
  */
case class TextResponse(firstLine:String,
                        headers: Array[(String, String)],
                        body:String) extends Response{

  var charset : String = ""

  def this(firstLine:String,
           headers: Array[(String, String)],
           body:String,
           charset:String){
    this(firstLine,headers,body)
    this.charset = charset
  }

  override def getHeadersString : String = {
    headers.map(RequestUtils.header2String).mkString("\r\n")
  }

  override def mkHttpBinary(encoding: String = "utf-8"): Array[Byte] = {
    mkHttpString().getBytes(encoding)
  }


  override def updateHeaders(newHeaders: Array[(String, String)]):TextResponse = {
    TextResponse(
      firstLine,
      newHeaders,
      body
    )
  }

  /**
    * format to http response format
    *
    * @return
    */
  override def mkHttpString(encoding:String = "utf-8"): String = {
    val str = new StringBuilder
    str.append(s"$firstLine\r\n")
      .append(getHeadersString)
      .append("\r\n" * 2)
      .append(s"$body").toString().trim
  }

  override def getContentLength :Int = body.length
}

object TextResponse{

  def apply(textResponse: TextResponse): TextResponse ={
    new TextResponse(
      textResponse.firstLine,
      textResponse.headers,
      textResponse.body
    )
  }

  def apply(firstLine: String,
            headers: Array[(String, String)],
            body: String,
            charset:String): TextResponse = {
    val r = new TextResponse(firstLine, headers, body)
    r.charset = charset
    r
  }

}
