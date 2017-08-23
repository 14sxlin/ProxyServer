package connection

import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/20.
  * DataTransfer due to transfer the data
  * between two endpoints
  */
class DataTransfer(client: ClientConnection,
                   server: ServerConnection) {

  val logger = LoggerFactory.getLogger(getClass)
  /**
    * Read data from client and transfer to
    * server. It may block while reading data
    * from client. Also note that this may throw
    * SocketException when connection has been
    * closed.
    *
    * @return
    */
  def transOnceFromClientToServer(): Int = {
    checkIfConnectionOpen()
    val data = client.readBinaryData()
    server.writeBinaryData(data)
    data.length
  }

  /**
    * Read data from server and transfer to
    * client. It may block while reading data
    * from server. Also note that this may throw
    * SocketException when connection has been
    * closed.
    *
    * @return
    */
  def transOnceFromServerToClient(): Int = {
    checkIfConnectionOpen()
    val data = server.readBinaryData()
    client.writeBinaryData(data)
    data.length
  }

  def communicate(): Unit = {
    checkIfConnectionOpen()
    val client2ServerThread = new Thread(new Runnable {
      override def run(): Unit ={
        logger.info("from client to server")
        transOnceFromClientToServer()
      }
    })
    val server2ClientThread = new Thread(new Runnable {
      override def run(): Unit ={
        logger.info("from server to client")
        transOnceFromServerToClient()
      }
    })

    client2ServerThread.start()
    server2ClientThread.start()
  }

  private def checkIfConnectionOpen() = {
    if(!(client.connectionOpen && server.connectionOpen))
      throw new IllegalStateException("connection not open")
  }

}
