package connection

import java.net.ServerSocket

import connection.control.ConnectionPool
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/10.
  */
case class ConnectionReceiver(port: Int, connectionPool: ConnectionPool) {

  private val logger = LoggerFactory.getLogger(getClass)

  def accept(): ClientConnection = {
    logger.info(s"start listening at $port")
    val serverSocket = new ServerSocket(port)
    val clientSocket = serverSocket.accept()

    logger.info(s"socket at ${clientSocket.getInetAddress} @ ${clientSocket.getPort}")
    val clientConnection = new ClientConnection(clientSocket)

    connectionPool.put(clientSocket.getInetAddress.toString, clientConnection)
    serverSocket.close()
    clientConnection
  }
}
