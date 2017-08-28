package connection

import java.net.Socket

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/20.
  */
case class ServerConnection(socket: Socket) extends TimeBasingAutoCloseConnection{


  override protected val name = s"Server-${socket.getInetAddress.toString}"

  override protected val resourceName = s"Server Socket"

  def this(host:String,port:Int) = {
    this(new Socket(host,port))
  }

  override def openConnection(): Unit = {
    if(connectionOpen)
      return
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
