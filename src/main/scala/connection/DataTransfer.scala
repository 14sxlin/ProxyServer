package connection

import java.net.SocketException

import constants.LoggerMark
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/20.
  * DataTransfer due to transfer the data
  * between two endpoints
  */
class DataTransfer(client: ClientConnection,
                   server: ServerConnection) {

  private val logger = LoggerFactory.getLogger(getClass)
  /**
    * Read data from client and transfer to
    * server. It may block while reading data
    * from client. Also note that this may throw
    * SocketException when connection has been
    * closed.
    *
    * @return transfer data(bytes) length
    */
  def transOnceFromClientToServer(): Int = {
    checkIfConnectionOpen()
    client.readBinaryData() match {
      case Some(data) =>
        logger.info(s"tran length = ${data.length}")
        server.writeBinaryData(data)
        data.length
      case None =>
        logger.warn("try to transfer empty from server to client")
        throw new SocketException("client has nothing to read")
    }

  }

  /**
    * Read data from server and transfer to
    * client. It may block while reading data
    * from server. Also note that this may throw
    * SocketException when connection has been
    * closed.
    *
    * @return transfer data(bytes) length
    */
  def transOnceFromServerToClient(): Int = {
    checkIfConnectionOpen()
    server.readBinaryData() match {
      case Some(data) =>
        client.writeBinaryData(data)
        data.length
      case None =>
        throw new SocketException("server has nothing to read")
    }
  }

  /**
    * This method should be block method.
    * It will allow other to decide whether
    * it should run in a thread or not.
    */
  def communicate(): Unit = {
    checkIfConnectionOpen()
    tryMaybeSocketClosed{
      while(true){
        val toServer = transOnceFromClientToServer()
        logger.info(s"${LoggerMark.up} length: $toServer")
        val toClient = transOnceFromServerToClient()
        logger.info(s"${LoggerMark.down} length: $toClient")
      }
    }
  }

  private def checkIfConnectionOpen(): Unit = {
    if(!(client.connectionOpen && server.connectionOpen))
      throw new IllegalStateException("connection not open")
  }

  private def tryMaybeSocketClosed(run : => Unit): Unit = {
    try{
      run
    }catch{
      case  e : SocketException =>
        logger.error(s"socket closed : ${e.getMessage}...")
    }
  }

}
