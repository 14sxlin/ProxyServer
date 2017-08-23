package connection

import java.io.{InputStream, OutputStream}

import org.slf4j.{Logger, LoggerFactory}
import scritps.HexAnalyse

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

  def readBinaryData(): Array[Byte] = {
    checkConnectionOpen()
    val total = ArrayBuffer[Byte]()
    var totalLen = 0
    val buffer = new Array[Byte](1024)

    @tailrec
    def readUtilBufferNotFull():Unit = {
      val length = in.read(buffer)
      if(length == -1)
        return
      total ++= buffer.slice(0, length)
      totalLen += length
      if(length == buffer.length)
        readUtilBufferNotFull()
    }

    readUtilBufferNotFull()
    logger.info(s"total length = $totalLen")
    logger.info(s"total data = \n${new String(total.toArray,"utf-8")}\n\n" +
      s"bytes: \n${HexAnalyse.toHex(total.toArray)}\n")
    total.toArray
  }

  def readTextData(encode:String = "gbk"): String = {
    checkConnectionOpen()
    val data = readBinaryData()
    val strData = new String(data,encode).trim
    strData
  }

  def writeBinaryData(data:Array[Byte]):Unit = {
    checkConnectionOpen()
    out.write(data)
    out.flush()
  }

  def writeTextData(data: String): Unit = {
    checkConnectionOpen()
    logger.info(s"write \n$data\nto end : length=${data.length}")
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
