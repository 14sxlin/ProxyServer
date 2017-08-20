package connection

import java.io.{InputStream, OutputStream}

import org.apache.commons.lang3.CharSet
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ArrayBuffer


/**
  * Created by sparr on 2017/8/20.
  */
trait Connection {

  val logger : Logger = LoggerFactory.getLogger(getClass)

  protected var out: OutputStream = _
  protected var in: InputStream = _
  protected var connectionOpen = false

  def openConnection():Unit = {
    connectionOpen = true
  }

  def readBinaryData(): Array[Byte] = {
    checkConnectionOpen()
    var total = ArrayBuffer[Byte]()
    var totalLen = 0
    val data = new Array[Byte](1024)
    var length = in.read(data, 0, data.length)
    logger.info(s"read : $length")
    while(length == data.length){
      total ++= data
      totalLen += length
      length = in.read(data, 0, data.length)
    }
    total ++= data
    totalLen += length
    logger.info(s"receive binary data length: $totalLen")
    length match {
      case -1 => Array.empty
      case _ => total.toArray
    }
  }

  def readTextData(encode:String = "gbk"): String = {
    checkConnectionOpen()
    val data = readBinaryData()
    val strData = new String(data,encode).trim
    logger.info(s"receive data : \n$strData")
    strData
  }

  def writeBinaryData(data:Array[Byte]):Unit = {
    checkConnectionOpen()
    out.write(data)
    out.flush()
  }

  def writeTextData(data: String): Unit = {
    checkConnectionOpen()
    logger.info(s"write \n$data to end : length=${data.length}")
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
