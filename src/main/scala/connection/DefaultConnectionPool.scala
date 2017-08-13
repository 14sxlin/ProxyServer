package connection

import java.net.Socket

import handler.header.DefaultHeaderChain

import scala.collection.mutable

/**
  * Created by sparr on 2017/8/12.
  */
class DefaultConnectionPool extends ConnectionPool{

  override protected val socketConnections: mutable.Map[Socket, SocketConnection]
        = mutable.Map[Socket,SocketConnection]()

  override def put(socket:Socket) : Unit = {
    val socketCon = new SocketConnection(socket)
    socketConnections += socket -> socketCon
    socketCon.startListen()
  }

  override def get(socket: Socket): SocketConnection = {
    socketConnections(socket)
  }
}
