package connection

import java.net.Socket

/**
  * Created by linsixin on 2017/8/11.
  */
abstract class ConnectionPool {

  protected val socketConnections :
    scala.collection.mutable.Map[Socket,SocketConnection]

  def put(socket:Socket)

  def get(socket: Socket):SocketConnection
}
