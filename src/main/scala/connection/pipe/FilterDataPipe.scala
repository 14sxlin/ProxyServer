package connection.pipe

import connection.{ClientConnection, ServerConnection}
import entity.request.{HeaderRecognizedRequest, RequestFactory}
import utils.RequestUtils


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
        val request = buffer.slice(0,length)
        val newRequest = whenReceiveRequestDo(request)
        logger.info(s"request:\n${new String(newRequest)}")
        sOut.write(newRequest)
        sOut.flush()
      }
    }
  }

  override protected val serverToClientDo = new Runnable {
    override def run(): Unit = tryMaybeSocketClosed{
      val buffer = new Array[Byte](2048)
      var length = 0
      while(length != -1){
        length = sIn.read(buffer)
        logger.info(s"trans <<<<<<<<<< $length")
        val part = buffer.slice(0,length)
        val newPart = whenReceivePartOfResponseDo(part)
        if(isResponseFinish(newPart))
          whenFinishReceiveResponseDo(newPart)
        cOut.write(newPart)
        cOut.flush()
      }
    }
  }

  protected def whenReceiveRequestDo(request:Array[Byte]): Array[Byte] ={
    RequestFactory.buildRequest(request) match {
      case recognizedRequest : HeaderRecognizedRequest =>
        RequestUtils.updateAbsoluteUriToRelative(recognizedRequest).mkHttpBinary()
      case _ =>
        request
    }

  }

  protected def whenReceivePartOfResponseDo : Array[Byte] => Array[Byte] = {
    part =>
//      logger.info(s"part of response")
      part
  }

  protected def isResponseFinish(bytes: Array[Byte]) : Boolean = {
    false
  }

  protected def whenFinishReceiveResponseDo : Array[Byte] => Array[Byte] = {
    lastPart =>
      logger.info("response finish !")
      lastPart
  }

  protected def shouldDoFilter(firstPartOfResponse:Array[Byte]):Boolean = {
    false
  }
}
