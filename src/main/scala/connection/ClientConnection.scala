package connection

import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.Socket

import connection.control.ActiveControl
import constants.{ConnectionConstants, Timeout}


/**
  * Created by linsixin on 2017/8/11.
  * This class holds the socket connection
  * Be responsible for reading data from
  * socket or sending data to client
  *
  */
class ClientConnection(val socket: Socket) extends ActiveControl with Connection{


  override val idleThreshold : Long = ConnectionConstants.idleThreshold

  override def openConnection(): Unit = {
    if(connectionOpen)
      return
    socket.setSoTimeout(Timeout.readTimeout)
    out = new BufferedOutputStream(socket.getOutputStream)
    in = new BufferedInputStream(socket.getInputStream)
    super.openConnection()
  }

  private def closeSocket(): Unit = {
    socket.close()
  }

  override def closeAllResource(): Unit = {
    super.closeAllResource()
    closeSocket()
  }

  override def closeWhenNotActive(): Unit = {
    closeAllResource()
  }
}
