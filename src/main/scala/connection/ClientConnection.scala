package connection

import java.net.Socket

import org.slf4j.{Logger, LoggerFactory}


/**
  * Created by linsixin on 2017/8/11.
  * This class holds the socket connection<br/>
  * Be responsible for reading data from socket <br/>
  * or sending data to client
  *
  */
class ClientConnection(val socket: Socket) extends TimeBasingAutoCloseConnection {

  override val logger: Logger = LoggerFactory.getLogger(getClass)

  override def openConnection(): Unit = {
    logger.info(s"start read in : ${socket.getInetAddress}")
    out = socket.getOutputStream
    in = socket.getInputStream
    logger.debug("setup stream success")
    super.openConnection()
  }

  private def closeSocket(): Unit = {
    socket.close()
  }

  override def closeAllResource(): Unit = {
    super.closeAllResource()
    closeSocket()
  }

  override def timeToClose(): Unit = {
    closeAllResource()
  }
}
