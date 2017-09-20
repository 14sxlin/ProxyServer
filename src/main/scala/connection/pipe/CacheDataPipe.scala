package connection.pipe

import connection.{ClientConnection, ServerConnection}
import org.apache.commons.lang3.StringUtils

/**
  * Created by linsixin on 2017/9/20.
  */
class CacheDataPipe(client:ClientConnection,
                    server:ServerConnection) extends FilterDataPipe(client,server){


  def isResponseContainHeader(response:Array[Byte]) : Boolean = {
    val strResponse = new String(response)
    val firstLine = StringUtils.substringBefore(strResponse,"\n").trim
    val parts =firstLine.split(" ")
    parts.length == 3 && parts(0).startsWith("HTTP/")
  }


  override protected def whenReceivePartOfResponseDo: Array[Byte] => Array[Byte] = {
    part =>
      if(isResponseContainHeader(part)){
        logger.info("It is response header")
        logger.info(s"${new String(part)}")
      }else{
        logger.info("not response header")
      }
      part
  }

  override protected def isResponseFinish(bytes: Array[Byte]): Boolean = {
    false
  }

  override protected def whenFinishReceiveResponseDo: Array[Byte] => Array[Byte] = {
    part =>
      part
  }

  override protected def shouldDoFilter(firstPartOfResponse: Array[Byte]): Boolean = {
    false
  }
}
