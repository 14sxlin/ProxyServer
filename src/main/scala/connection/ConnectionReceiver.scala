package connection

import java.net.ServerSocket

import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/10.
  */
case class ConnectionReceiver(port: Int) {

   def accept(): ClientConnection = {
    this.synchronized{
      val serverSocket = new ServerSocket(port)
      val clientSocket = serverSocket.accept()
      val clientConnection = ClientConnection(clientSocket)
      clientConnection.name = s"${clientSocket.getInetAddress}:${clientSocket.getPort}"
      serverSocket.close()
      clientConnection
    }
  }
}
