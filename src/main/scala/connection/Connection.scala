package connection

import java.io.{InputStream, OutputStream}

import org.slf4j.{Logger, LoggerFactory}
import utils.http.HexUtils

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer


/**
  * Created by linsixin on 2017/8/20.
  * Connection represent the socket connection
  * which able to read or write data from or to
  * the other side
  */
trait Connection {

  val logger : Logger = LoggerFactory.getLogger(getClass)

  protected var out: OutputStream = _
  protected var in: InputStream = _
  var connectionOpen = false

  /**
    * Init inputstream and outputstream
    * and set open connectionOpen true
    */
  def openConnection():Unit = {
    connectionOpen = true
  }

  /**
    * read data from socket
    * @return return read bytes and remember to check
    *         whether the data is empty
    */
  def readBinaryData(): Option[Array[Byte]] = {
    checkConnectionOpen()
    val total = ArrayBuffer[Byte]()
    var totalLen = 0
    val buffer = new Array[Byte](1024)

    @tailrec
    def readUntilBufferNotFull():Unit = {
      val length = in.read(buffer)
      if(length == -1){
        logger.info("read nothing ,socket close, get -1")
        return
      }

      total ++= buffer.slice(0, length)
      totalLen += length
      if(length == buffer.length)
        readUntilBufferNotFull()
    }
    readUntilBufferNotFull()
    if(totalLen == 0)
      return None
    logger.info(s"total read length = $totalLen ")
//    logger.info(s"total read data(gbk) : \n${new String(total.toArray,"gbk")}\n")
//    logger.info(s"total read data(utf-8): \n${new String(total.toArray,"utf-8")}\n" +
//      s"----------------------------------------------\r\n" +
//      s"bytes: \n${HexUtils.toHex(total.toArray)}")
    Some(total.toArray)
  }

  def readTextData(encode:String = "utf-8"): Option[String] = {
    checkConnectionOpen()
    readBinaryData() match {
      case None => None
      case Some(data) =>
        val strData = new String(data,encode).trim
        Some(strData)
    }
  }

  def writeBinaryData(data:Array[Byte]):Unit = {
    checkConnectionOpen()
    out.write(data)
    out.flush()
  }

  def writeTextData(data: String): Unit = {
    checkConnectionOpen()
//    logger.info(s"write \n$data\nto end : length=${data.length}")
    logger.info(s"write ${data.length} of data  to end ")
    writeBinaryData(data.getBytes)
  }

  private def checkConnectionOpen() = {
    if(!connectionOpen)
      throw new IllegalStateException("connection not open")
  }

  def closeAllResource():Unit = {
    out.close()
    in.close()
  }

}
