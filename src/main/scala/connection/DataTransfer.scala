package connection

import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.{SocketException, SocketTimeoutException}

import constants.LoggerMark
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/20.
  * DataTransfer due to transfer the data
  * between two endpoints
  */
class DataTransfer(client: ClientConnection,
                   server: ServerConnection) {

  private val logger =
    LoggerFactory.getLogger(s"${server.socket.getInetAddress}" +
      s":${server.socket.getPort}")
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
    * This method will start two thread which
    * respectively listens input stream of
    * server and client connection.Once there
    * are some data,they will transfer to other
    * connection directly.
    */
  def startCommunicate(): Unit = {
    checkIfConnectionOpen()
    val cIn = new BufferedInputStream(client.getInputStream)
    val cOut = new BufferedOutputStream(client.getOutputStream)
    val sIn = new BufferedInputStream(server.getInputStream)
    val sOut = new BufferedOutputStream(server.getOutputStream)
    val clientToServerThread = new Thread(new Runnable {
      override def run(): Unit = tryMaybeSocketClosed{
        val buffer = new Array[Byte](2048)
        var length = 0
        while(length != -1){
          length = cIn.read(buffer)
          logger.info(s"${LoggerMark.up} $length")
          sOut.write(buffer.slice(0,length))
          sOut.flush()
        }
      }
    })
    val serverToClientThread = new Thread(new Runnable {
      override def run(): Unit = tryMaybeSocketClosed{
        val buffer = new Array[Byte](2048)
        var length = 0
        while(length != -1){
          length = sIn.read(buffer)
          logger.info(s"${LoggerMark.down} $length")
          cOut.write(buffer.slice(0,length))
          cOut.flush()
        }
      }
    })
    clientToServerThread.setName("Client2Server")
    serverToClientThread.setName("Server2Client")
    clientToServerThread.start()
    serverToClientThread.start()
  }

  private def checkIfConnectionOpen(): Unit = {
    client.checkConnectionOpen()
    server.checkConnectionOpen()
  }

  private def tryMaybeSocketClosed(run : => Unit): Unit = {
    try{
      run
    }catch{
      case  e : SocketException =>
        logExceptionMessageAndCloseResources(e)
      case e : SocketTimeoutException =>
        logExceptionMessageAndCloseResources(e)
    }
  }

  private def logExceptionMessageAndCloseResources(e:Exception):Unit = {
    logger.error(s"${LoggerMark.resource}: ${e.getMessage}... close all sockets")
    client.closeAllResource()
    server.closeAllResource()
  }
}