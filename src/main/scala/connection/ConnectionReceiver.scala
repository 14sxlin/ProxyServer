package connection

import java.net.ServerSocket

import org.slf4j.LoggerFactory

/**
  * Created by sparr on 2017/8/10.
  */
object ConnectionReceiver {

  private val logger = LoggerFactory.getLogger(getClass)

  val port689 = 689
  val connectionPool = new DefaultConnectionPool

  def accept() : Unit = {
    logger.info(s"start listening at $port689")
    val serverSocket = new ServerSocket(port689)
    val clientSocket = serverSocket.accept()
    connectionPool.put(clientSocket)
    logger.info(s"connect : ${clientSocket.getInetAddress}")
    serverSocket.close()
  }
}
