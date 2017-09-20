package connection.pipe

import java.io.InputStream

import connection.{ClientConnection, ServerConnection}


/**
  * Created by linsixin on 2017/9/20.
  */
class FilterDataPipe(client: ClientConnection,
                     server: ServerConnection) extends DataPipe(client,server){


  override protected val clientToServerDo : Runnable = new Runnable {
    override def run(): Unit = tryMaybeSocketClosed{
      val buffer = new Array[Byte](1024 * 10)
      var length = 0
      while(length != -1){
        length = cIn.read(buffer)
        logger.info(s"trans >>>>>>>>>> $length")
        val part = buffer.slice(0,length)
        whenReceiveRequestDo(part)
        sOut.write(part)
        sOut.flush()
      }
    }
  }

  override protected val serverToClientDo = new Runnable {
    override def run(): Unit = tryMaybeSocketClosed{
      val buffer = new Array[Byte](1024 * 10)
      var length = 0
      while(length != -1){
        length = cIn.read(buffer)
        logger.info(s"trans >>>>>>>>>> $length")
        val part = buffer.slice(0,length)
        whenReceivePartOfResponseDo(part)
        if(isResponseFinish(part))
          whenFinishReceiveResponseDo(part)
        sOut.write(part)
        sOut.flush()
      }
    }
  }

  protected def readOnce(in:InputStream) : Array[Byte] = {
    val buffer = new Array[Byte](1024 * 10)
    val length = in.read(buffer)
    if (length == -1)
      Array.empty[Byte]
    else buffer.slice(0, length)
  }

  protected def whenReceiveRequestDo(request:Array[Byte]): Unit ={
    logger.info(s"receive request :\n '${new String(request)}'")
  }

  protected def processFirstPartOfResponse(part:Array[Byte]):Unit = {
    logger.info("process first part of response")
  }

  protected def sendFirstPartOfResponseToClient(part:Array[Byte]):Unit = {
    logger.info("send first response to client")
  }

  protected def whenReceivePartOfResponseDo : Array[Byte] => Unit = {
    part =>
//      logger.info(s"part of response")
  }

  protected def isResponseFinish(bytes: Array[Byte]) : Boolean = {
    false
  }

  protected def whenFinishReceiveResponseDo : Array[Byte] => Unit = {
    lastPart =>
      logger.info("response finish !")
  }


  /**
    * This method will start two thread which
    * respectively listens input stream of
    * server and client connection.Once there
    * are some data,they will transfer to other
    * connection directly.
    */
  override def startCommunicate(): Unit = {
    checkIfConnectionOpen()
    val firstPartOfResponse = getFirstPartOfResponse
    if(firstPartOfResponse.isEmpty){
      logger.error("first part of response is empty, close resource")
      closeResource()
      return
    }
    if(shouldDoFilter(firstPartOfResponse)){
      processFirstPartOfResponse(firstPartOfResponse)
      create2ThreadToTransData(
        clientToServerDo = clientToServerDo,
        serverToClientDo = serverToClientDo
      )
    }else{
      create2ThreadToTransData(
        clientToServerDo = clientToServerDo,
        serverToClientDo = new Runnable {
          override def run(): Unit = tryMaybeSocketClosed{
            val buffer = new Array[Byte](2048)
            var length = 0
            while(length != -1){
              length = sIn.read(buffer)
              logger.info(s"trans <<<<<<<<<< $length")
              val part = buffer.slice(0,length)
              logger.info(s"part of content : ${new String(part)}")
              cOut.write(part)
              cOut.flush()
            }
          }
        })
    }
  }

  protected def getFirstRequest : Array[Byte] = {
    logger.info("get first request")
    readOnce(cIn)
  }

  protected def getFirstPartOfResponse : Array[Byte] = {
    client.readBinaryData() match {
      case Some(data) => data
      case None => Array.empty[Byte]
    }
  }

  protected def shouldDoFilter(firstPartOfResponse:Array[Byte]):Boolean = {
    false
  }
}
