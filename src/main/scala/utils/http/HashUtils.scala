package utils.http

import java.net.Socket

import connection.ClientConnection
import entity.request.Request


/**
  * Created by sparr on 2017/8/25.
  */
object HashUtils {

  def getHash(con: ClientConnection, request: Request) : String = {
    s"${con.socket.getLocalAddress.toString}:${con.socket.getLocalPort}-" +
    s"${request.getHost.getOrElse("unknown-host")}"
  }
}
