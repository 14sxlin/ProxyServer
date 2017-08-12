package connection

import java.net.Socket

import scala.collection.mutable.ArrayBuffer

/**
  * Created by linsixin on 2017/8/11.
  */
abstract class ConnectionPool {

  protected val socketConnections : ArrayBuffer[SocketConnection] =
    ArrayBuffer[SocketConnection]()

  def add(socket:Socket)
}
