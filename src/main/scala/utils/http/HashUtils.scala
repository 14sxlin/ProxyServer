package utils.http

import connection.ClientConnection
import entity.request.Request


/**
  * Created by linsixin on 2017/8/25.
  */
object HashUtils {

  def getHash(con: ClientConnection, request: Request) : String = {
    val addr = con.socket.getInetAddress
    if( con!=null && addr!=null && request!=null){
      s"${addr.toString}-" +
        s"${request.getHost.getOrElse("unknown-host")}"
    }
    else throw new IllegalArgumentException("con and request should not be null")
  }
}
