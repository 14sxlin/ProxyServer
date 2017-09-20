package connection.pipe

import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.{SocketException, SocketTimeoutException}

import connection.Connection
import constants.LoggerMark
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/20.
  * DataPipe due to transfer the data
  * between two endpoints
  */
class DataPipe(client: Connection,
               server: Connection) {

  protected val logger:Logger = LoggerFactory.getLogger(getClass)

  protected val cIn = new BufferedInputStream(client.getInputStream)
  protected val cOut = new BufferedOutputStream(client.getOutputStream)
  protected val sIn = new BufferedInputStream(server.getInputStream)
  protected val sOut = new BufferedOutputStream(server.getOutputStream)

  protected val clientToServerDo : Runnable = new Runnable {
    override def run(): Unit = tryMaybeSocketClosed{
      val buffer = new Array[Byte](2048)
      var length = 0
      while(length != -1){
        length = cIn.read(buffer)
        logger.info(s"trans >>>>>>>>>> $length")
        val part = buffer.slice(0,length)
        logger.info(s"part of request :\n${new String(part)}")
        sOut.write(part)
        sOut.flush()
      }
    }
  }
  protected val serverToClientDo = new Runnable {
    override def run(): Unit = tryMaybeSocketClosed{
      val buffer = new Array[Byte](2048)
      var length = 0
      while(length != -1){
        length = sIn.read(buffer)
        logger.info(s"trans <<<<<<<<<< $length")
        val part = buffer.slice(0,length)
        logger.info(s"part of response :\n${new String(part)}")
        cOut.write(part)
        cOut.flush()
      }
    }
  }

   /* This method will start two thread which
    * respectively listens input stream of
    * server and client connection.Once there
    * are some data,they will transfer to other
    * connection directly.
    */
  def startCommunicate(): Unit = {
    checkIfConnectionOpen()
    create2ThreadToTransData(clientToServerDo,serverToClientDo)
  }

  protected def startRequestThread(clientToServerDo:Runnable) : Unit = {
    val requestThread = new Thread(clientToServerDo)
    requestThread.setName(s"${client.name} <?>")
    requestThread.start()

  }

  protected def startResponseThread(serverToClientDo:Runnable) :Unit = {
    val responseThread = new Thread(serverToClientDo)
    responseThread.setName(s"${server.name} <!>")
    logger.info(s"${client.name} -> ${server.name} start communicate")
    responseThread.start()
  }

  protected def create2ThreadToTransData(clientToServerDo:Runnable,
                                         serverToClientDo:Runnable):Unit = {
    startRequestThread(clientToServerDo)
    startResponseThread(serverToClientDo)
  }

  protected def checkIfConnectionOpen(): Unit = {
    client.checkConnectionOpen()
    server.checkConnectionOpen()
  }

  protected def tryMaybeSocketClosed(run : => Unit): Unit = {
    try{
      run
    }catch{
      case  e : SocketException =>
        logExceptionMessageAndCloseResources(e)
      case e : SocketTimeoutException =>
        logExceptionMessageAndCloseResources(e)
    }
  }

  protected def logExceptionMessageAndCloseResources(e:Exception):Unit = {
    logger.error(s"${LoggerMark.resource}: ${e.getMessage}... close")
    client.closeAllResource()
    server.closeAllResource()
  }

  def closeResource():Unit = {
    server.closeAllResource()
    client.closeAllResource()
  }
}