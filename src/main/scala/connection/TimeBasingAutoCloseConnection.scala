package connection

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by sparr on 2017/8/20.
  */
trait TimeBasingAutoCloseConnection extends Connection with CloseWhenNotActive{

  override val logger : Logger = LoggerFactory.getLogger(getClass)

  override def readBinaryData(): Array[Byte] ={
    updateActiveTime()
    super.readBinaryData()
  }

  override def writeBinaryData(data: Array[Byte]): Unit = {
    updateActiveTime()
    super.writeBinaryData(data)
  }
}
