package entity.response

import utils.RequestUtils

/**
  * Created by linsixin on 2017/8/28.
  */
case class BinaryResponse(firstLine:String,
                          headers: Array[(String, String)],
                          body:Array[Byte]) extends Response{

  override def mkHttpBinary(encoding:String = "utf-8") : Array[Byte] = {
    (firstLine + "\r\n" +
      headers.map(RequestUtils.header2String).mkString("\r\n") +
      "\r\n"*2)
      .getBytes(encoding) ++ body
  }

  override def mkHttpString(encoding:String = "utf-8") = {
    firstLine + "\r\n" +
      headers.map(RequestUtils.header2String).mkString("\r\n") +
      "\r\n"*2 +
      new String(body,encoding)
  }

  override def getContentLength = body.length
}
