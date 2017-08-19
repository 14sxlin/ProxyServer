package connection

import scala.collection.mutable

/**
  * Created by sparr on 2017/8/12.
  */
class DefaultConnectionPool extends ConnectionPool{

  override val socketConnections: mutable.Map[String, ClientConnection]
  = mutable.Map[String, ClientConnection]()

  override def put(id: String, socketCon: ClientConnection): Unit = {
    socketConnections += (id -> socketCon)
  }

  override def get(id: String): ClientConnection = {
    socketConnections(id)
  }
}
