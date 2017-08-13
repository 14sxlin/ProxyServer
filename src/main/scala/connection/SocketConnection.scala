package connection

import java.io._
import java.net.Socket

import constants.HttpRequestMethod
import entity.{Request, Response}
import handler.header.DefaultHeaderChain
import org.slf4j.LoggerFactory
import task.TaskFactory
import utils.http.RequestBuilder

import scala.collection.mutable.ArrayBuffer


/**
  * Created by linsixin on 2017/8/11.
  * This class holds the socket connection<br/>
  *
  */
class SocketConnection(val socket:Socket) {

  private val logger = LoggerFactory.getLogger(getClass)

  private var readThread: Thread = _
  private var writer : PrintWriter = _
  private var in : InputStream = _
  private var dontCloseConnection2Client = false

  def startListen():Unit = {
    logger.info(s"start read in : ${socket.getInetAddress}")
    setupStream()
    startListenThread()
  }

  private def setupStream() : Unit = {
    writer = new PrintWriter(socket.getOutputStream)
    logger.debug("setup stream success")
  }

  private def startListenThread() : Unit = {
    val listenTask = new Runnable {
      override def run()  {
        listenAndBuildRequestTask()
      }
    }
    readThread = new Thread(listenTask)
    readThread.start()
  }

  private def listenAndBuildRequestTask() : Unit = {

    var request = RequestBuilder.buildRequest(readRequestRawString(),
      DefaultHeaderChain.firstOfRequestHeaderHandlerChain)

    if(request.method == HttpRequestMethod.CONNECT){
//      responseProxyConnect(request)
//      request = RequestBuilder.buildRequest(reader,
//        DefaultHeaderChain.firstOfRequestHeaderHandlerChain)
      logger.debug("skip connect")
      return
    }
    val task = TaskFactory.createTask(request)
    task.onSuccess = response2Client
    task.onFail = (e) =>{
      logger.error("",e)
      closeConnection()
    }
    task.begin()

  }

  private def readRequestRawString() = {
    val requestRawData = new Array[Byte](1024*100)
    in = socket.getInputStream
    in.read(requestRawData,0,requestRawData.length)
    val data = new String(requestRawData).trim
    logger.info(s"raw request: $data")
    data
  }

  private def responseProxyConnect(request:Request) : Boolean = {
    val success200 = "HTTP/1.1 200 Connection established\r\n"
    writer.append(success200)
    writer.flush()
    dontCloseConnection2Client = true
    true
  }

  private def response2Client(response: Response) = {
    logger.info(s"response length : ${response.body.length}")
    val handler = DefaultHeaderChain.firstOfResponseHeaderHandlerChain
    val newHeader = handler.handle(response.headers)
    response.headers = newHeader
    writer.append(response.mkString)
    writer.flush()

    closeConnection()
  }

  private def closeConnection(): Unit ={
    writer.close()
    in.close()
    socket.close()
  }



}
