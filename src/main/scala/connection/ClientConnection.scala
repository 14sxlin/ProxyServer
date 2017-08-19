package connection

import java.io._
import java.net.Socket

import org.slf4j.LoggerFactory


/**
  * Created by linsixin on 2017/8/11.
  * This class holds the socket connection<br/>
  * Be responsible for reading data from socket <br/>
  * or sending data to client
  *
  */
class ClientConnection(val socket: Socket) {

  private val logger = LoggerFactory.getLogger(getClass)

  private var writer: PrintWriter = _
  private var in: InputStream = _

  def openConnection(): Unit = {
    logger.info(s"start read in : ${socket.getInetAddress}")
    setupStream()
  }

  private def setupStream(): Unit = {
    writer = new PrintWriter(socket.getOutputStream)
    in = socket.getInputStream
    logger.debug("setup stream success")
  }

  def readData(): String = {
    val data = new Array[Byte](1024)
    in = socket.getInputStream
    val end = in.read(data, 0, data.length)
    val strData = new String(data).trim
    logger.info(s"data : $strData : end: $end")
    strData
  }

  def writeData(data: String): Unit = {
    logger.info(s"write data to client : length=${data.length}")
    writer.append(data)
    writer.flush()
  }

  def closeIn() {
    in.close()
  }

  def closeOut(): Unit = {
    writer.close()
  }

  def closeSocket(): Unit = {
    socket.close()
  }

  def closeAllResource(): Unit = {
    closeIn()
    closeOut()
    closeSocket()
  }


}
