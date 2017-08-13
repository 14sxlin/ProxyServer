package connection

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket

import entity.Response
import handler.header.DefaultHeaderChain
import org.slf4j.LoggerFactory
import task.TaskFactory
import utils.http.RequestBuilder


/**
  * Created by linsixin on 2017/8/11.
  * This class holds the socket connection<br/>
  *
  */
class SocketConnection(val socket:Socket) {

  private val logger = LoggerFactory.getLogger(getClass)

  private var readThread: Thread = _
  private var reader : BufferedReader = _
  private var writer : PrintWriter = _

  def startListen():Unit = {
    setupStream()
    startListenThread()
  }

  private def setupStream() : Unit = {
    reader = new BufferedReader(
      new InputStreamReader(socket.getInputStream))
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

  private def listenAndBuildRequestTask() = {
    val request = RequestBuilder.buildRequest(reader,
      DefaultHeaderChain.firstOfRequestHeaderHandlerChain)
    val task = TaskFactory.createTask(request)
    task.onSuccess = response2Client
    task.begin()
  }

  private def response2Client(response: Response) = {
    val handler = DefaultHeaderChain.firstOfResponseHeaderHandlerChain
    val newHeader = handler.handle(response.headers)
    response.headers = newHeader
    writer.append(response.mkString)
    writer.flush()
    writer.close()
    reader.close()
    socket.close()
  }



}
