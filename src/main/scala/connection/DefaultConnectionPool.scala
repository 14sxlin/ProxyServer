package connection

import java.net.Socket

import handler.header.DefaultHeaderChain

/**
  * Created by sparr on 2017/8/12.
  */
class DefaultConnectionPool extends ConnectionPool{

  override def add(socket:Socket) : Unit = {
    socketConnections += new SocketConnection(socket)
  }
}
