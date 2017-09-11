package entity.response

import utils.RequestUtils

/**
  * Created by linsixin on 2017/8/28.
  */
case class BinaryResponse(firstLine:String,
                          headers: Array[(String, String)],
                          body:Array[Byte]) extends Response{


  override def getHeadersString : String = {
    headers.map(RequestUtils.header2String).mkString("\r\n")
  }


  override def updateHeaders(newHeaders: Array[(String, String)]):BinaryResponse = {
    BinaryResponse(firstLine,newHeaders,body)
  }

  override def mkHttpBinary(encoding:String = "utf-8") : Array[Byte] = {
    (firstLine + "\r\n" +
      getHeadersString +
      "\r\n"*2)
      .getBytes(encoding) ++ body
  }

  override def mkHttpString(encoding:String = "utf-8"): String = {
    firstLine + "\r\n" +
      headers.map(RequestUtils.header2String).mkString("\r\n") +
      "\r\n"*2 +
      new String(body,encoding)
  }

  override def getContentLength: Int = body.length
}

object BinaryResponse{

  def apply(binaryResponse: BinaryResponse,firstLine:String): BinaryResponse = {
    new BinaryResponse(
      firstLine,
      binaryResponse.headers,
      binaryResponse.body
    )
  }


}
