package connection

import java.net.Socket

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by sparr on 2017/8/20.
  */
case class ServerConnection(socket: Socket) extends TimeBasingAutoCloseConnection{

  def this(host:String,port:Int) = {
    this(new Socket(host,port))
  }

  override def openConnection(): Unit = {
    out = socket.getOutputStream
    in = socket.getInputStream
    super.openConnection()
  }

  private def closeSocket(): Unit = {
    socket.close()
  }

  override def closeAllResource(): Unit = {
    super.closeAllResource()
    closeSocket()
  }

  override def timeToClose(): Unit = {
    closeAllResource()
  }
}
