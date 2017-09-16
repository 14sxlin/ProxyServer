import java.net.SocketException
import java.util.concurrent.ArrayBlockingQueue

import connection._
import connection.dispatch.{ClientRequestDispatcher, RequestConsumeThread}
import connection.pool.{ClientServiceUnitPool, ServerConnectionPool}
import constants.{ConnectionConstants, LoggerMark}
import controller.RequestController
import entity.request._
import http.{CompressConnectionPoolClient, RequestProxy}
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/25.
  */
object HttpProxyServerRun2 extends App {

  val logger = LoggerFactory.getLogger(getClass)
  val port = 689

  val requestQueue = new ArrayBlockingQueue[RequestUnit](ConnectionConstants.maxConnection)
  val clientPool = new ClientServiceUnitPool
  val requestDispatcher = new ClientRequestDispatcher(clientPool)
  val serverConPool = new ServerConnectionPool[ServerConnection]()
  val controller = new RequestController(requestDispatcher,requestQueue)

  for( i <- 1 to 3){
    val connectionPoolingClient = new CompressConnectionPoolClient
//    val connectionPoolingClient = new ConnectionPoolClient
    val requestProxy = new RequestProxy(connectionPoolingClient)
    val requestConsumeThread = new RequestConsumeThread(clientPool,requestQueue,requestProxy)
    requestConsumeThread.setName(s"Request-Consume-Thread$i")
    requestConsumeThread.setDaemon(true)
    requestConsumeThread.setPriority(10)
    requestConsumeThread.start()
  }

  val beginTask = new Runnable {
    override def run() : Unit = {
      while(true){
        begin()
      }
    }
  }
  val t = new Thread(beginTask)
  t.setName("Socket-Accept-Thread")
  t.start()

  val runGroup = new ThreadGroup("Process-Theads")

  def begin(): Unit = {
    val receiver = ConnectionReceiver(port)
    val clientConnection = receiver.accept()
    clientConnection.openConnection()

    val processRun = new Runnable {
      override def run(): Unit =
        try{
          controller.startProcess(clientConnection)
        }catch {
          case e: SocketException =>
            logger.warn(s"socket has closed, ${e.getMessage}")
        }
    }


    val processThread = new Thread(runGroup,processRun)
    processThread.setName(s"Process-Thread-" +
      s"${clientConnection.socket.getRemoteSocketAddress.toString}")
    processThread.setPriority(3)
    processThread.start()
    logger.info(s"${LoggerMark.resource} process-active-count: ${runGroup.activeCount()}")
  }


  }
