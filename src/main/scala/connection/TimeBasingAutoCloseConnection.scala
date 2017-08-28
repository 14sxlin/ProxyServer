package connection

/**
  * Created by linsixin on 2017/8/20.
  */
trait TimeBasingAutoCloseConnection extends Connection with CloseWhenNotActive{

  override def readBinaryData(): Option[Array[Byte]] ={
    updateActiveTime()
    super.readBinaryData()
  }

  override def writeBinaryData(data: Array[Byte]): Unit = {
//    updateActiveTime()
    super.writeBinaryData(data)
  }
}
