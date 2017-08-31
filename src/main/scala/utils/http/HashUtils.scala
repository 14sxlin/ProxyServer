package utils.http

import connection.ClientConnection
import entity.request.Request


/**
  * Created by linsixin on 2017/8/25.
  */
object HashUtils {

  def getHash(con: ClientConnection, request: Request) : String = {
    if(con == null || con.socket == null || request == null)
      throw new IllegalArgumentException("con and request should not be null")
    val addr = con.socket.getInetAddress
    val port = con.socket.getPort
    if( addr!=null){
      s"${addr.toString}:$port-" +
        s"${request.getHost.getOrElse("unknown-host")}"
    }
    else throw new IllegalArgumentException("con and request should not be null")
  }
}
