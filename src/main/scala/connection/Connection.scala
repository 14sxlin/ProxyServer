package connection

import java.io.{InputStream, OutputStream}

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer


/**
  * Created by linsixin on 2017/8/20.
  * Connection represent the socket connection
  * which able to read or write data from or to
  * the other side
  */
trait Connection {

  var name : String = _
  protected var out: OutputStream = _
  protected var in: InputStream = _
  var connectionOpen = false

  /**
    * Init input stream and output stream
    * and set open connectionOpen true
    * remember to setSoTimeOut
    */
  def openConnection():Unit = {
    connectionOpen = true
  }

  def setReadTimeout(timeout:Int):Unit

  /**
    * read data from socket
    * @return return read bytes and remember to check
    *         whether the data is empty
    */
  def readBinaryData(): Option[Array[Byte]] = {
    checkConnectionOpen()
    val total = ArrayBuffer[Byte]()
    var totalLen = 0
    val buffer = new Array[Byte](10240)

    @tailrec
    def readUntilBufferNotFull():Unit = {
      val length = in.read(buffer)
      if(length == -1){
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

  def writeTextData(data: String, encoding:String = "utf-8"): Unit = {
    checkConnectionOpen()
    writeBinaryData(data.getBytes(encoding))
  }

  def checkConnectionOpen(): Unit = {
    if(!connectionOpen)
      throw new IllegalStateException("connection not open")
  }


  def getInputStream : InputStream = in

  def getOutputStream : OutputStream = out

  def closeAllResource():Unit = {
    out.close()
    in.close()
  }

}
