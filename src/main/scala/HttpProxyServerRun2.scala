import java.net.SocketException
import java.util.concurrent.ArrayBlockingQueue

import config.MyDefaultConfig.config
import connection._
import connection.dispatch.RequestDispatcher
import connection.pool.{ClientContextUnitPool, ServerConnectionPool}
import constants.ConfigNames
import controller.RequestController
import controller.thread.RequestConsumeThread
import http.{ConnectionPoolClient, RequestProxy}
import model.RequestUnit
import org.slf4j.LoggerFactory
/**
  * Created by linsixin on 2017/8/25.
  */
object HttpProxyServerRun2 extends App {

  val logger = LoggerFactory.getLogger(getClass)

  val requestThreadCount = config.getInt(ConfigNames.requestThreadCount)
  val port = config.getInt(ConfigNames.port)

  logger.info(s"start listen at $port")

  val requestQueue = new ArrayBlockingQueue[RequestUnit](config.getInt(ConfigNames.maxConnection))
  val clientPool = new ClientContextUnitPool

//  val httpCache = new HttpCache("D:\\test-cache\\proxyserver")
//  val cacheHandler = new CacheHandler(httpCache)
//  val requestDispatcher = new CacheRequestDispatcher(clientPool)
//  val controller = new CacheRequestController(requestDispatcher,requestQueue,cacheHandler)

  val requestDispatcher = new RequestDispatcher(clientPool)
  val controller = new RequestController(requestDispatcher,requestQueue)
  val serverConPool = new ServerConnectionPool[ServerConnection]()

  for( i <- 1 to requestThreadCount){
//    val connectionPoolingClient = new CompressConnectionPoolClient
    val connectionPoolingClient = new ConnectionPoolClient
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

  def begin(): Unit = this.synchronized{
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
    processThread.start()
//    logger.info(s"${LoggerMark.resource} process-active-count: ${runGroup.activeCount()}")
  }


  }
