package connection

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket

import constants.HttpRequestMethod
import entity.Request
import exception.NotHeaderException
import handler.header.{DefaultHeaderChain, HeaderHandler}
import org.slf4j.LoggerFactory
import task.{GetTask, PostTask, TaskFactory}
import utils.http.RequestBuilder

import scala.collection.mutable.ArrayBuffer


/**
  * Created by sparr on 2017/8/11.
  * This class holds the socket connection<br/>
  *
  */
class SocketConnection(socket:Socket) {

  private val logger = LoggerFactory.getLogger(getClass)

  private val NOT_FOUND = -1

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
    TaskFactory.createTask(request)
  }



}
