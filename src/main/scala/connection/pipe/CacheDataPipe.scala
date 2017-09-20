package connection.pipe

import connection.{ClientConnection, ServerConnection}

/**
  * Created by linsixin on 2017/9/20.
  */
class CacheDataPipe(client:ClientConnection,
                    server:ServerConnection) extends FilterDataPipe(client,server){

  override protected def processFirstPartOfResponse(part: Array[Byte]): Unit = {

  }

  def isResponseCacheable(part:Array[Byte]):Boolean = {
    val reponseWithHeader = new String(part)
    logger.info(s"first part of response :\n $reponseWithHeader")
    false
  }

  override protected def sendFirstPartOfResponseToClient(part: Array[Byte]): Unit = {

  }

  override protected def whenReceivePartOfResponseDo: (Array[Byte]) => Unit = {
    part =>
  }

  override protected def isResponseFinish(bytes: Array[Byte]): Boolean = {
    false
  }

  override protected def whenFinishReceiveResponseDo: (Array[Byte]) => Unit = {
    part =>

  }

  override protected def shouldDoFilter(firstPartOfResponse: Array[Byte]): Boolean = {
    false
  }
}
