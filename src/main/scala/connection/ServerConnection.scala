package connection

import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.Socket

/**
  * Created by linsixin on 2017/8/20.
  * This represent the connection to server.
  * It will be put into a connection pool,so
  * its read timeout won't be set or may be
  * can set longer time because it may be reused.
  */
case class ServerConnection(socket: Socket) extends Connection{

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

  def this(socket: Socket,name:String) = {
    this(socket)
    this.name = name
  }

  override def setReadTimeout(timeout: Int): Unit = {
    socket.setSoTimeout(timeout)
  }


  override def openConnection(): Unit = {
    if(connectionOpen)
      return
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
}
