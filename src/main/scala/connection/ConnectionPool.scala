package connection

/**
  * Created by linsixin on 2017/8/11.
  */
trait ConnectionPool {

  protected val socketConnections :
    scala.collection.mutable.Map[String, ClientConnection]

  def put(id: String, socket: ClientConnection)

  def get(id: String): ClientConnection
}
