package connection

import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.Socket

import connection.control.ActiveControl
import constants.{ConnectionConstants, Timeout}

/**
  * Created by linsixin on 2017/8/20.
  */
case class ServerConnection(socket: Socket) extends ActiveControl with Connection{


  override protected val idleThreshold : Long = ConnectionConstants.idleThreshold

//  override protected val name = s"Server-${socket.getInetAddress.toString}"

//  override protected val resourceName = s"Server Socket"

  /**
    * auto create a new socket
    * but unable to set connection
    * timeout
    * @param host host
    * @param port port
    */
  def this(host:String,port:Int) = {
    this(new Socket(host,port))
  }

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
