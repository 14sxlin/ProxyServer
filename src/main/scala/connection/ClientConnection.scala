package connection

import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.Socket

import constants.Timeout


/**
  * Created by linsixin on 2017/8/11.
  * This class holds the socket connection
  * Be responsible for reading data from
  * socket or sending data to client
  *
  */
case class ClientConnection(socket: Socket) extends Connection{

  socket.setSoTimeout(Timeout.readTimeout) // set default timeout

  def this(socket: Socket,name:String) = {
    this(socket)
    this.name = name
  }

  override def openConnection(): Unit = {
    if(connectionOpen)
      return
    out = new BufferedOutputStream(socket.getOutputStream)
    in = new BufferedInputStream(socket.getInputStream)
    super.openConnection()
  }

  override def setReadTimeout(timeout:Int): Unit ={
    socket.setSoTimeout(timeout)
  }

  private def closeSocket(): Unit = {
    socket.close()
  }

  override def closeAllResource(): Unit = {
    super.closeAllResource()
    closeSocket()
  }

}
