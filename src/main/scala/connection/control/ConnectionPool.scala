package connection.control

/**
  * Created by linsixin on 2017/8/11.
  */
trait ConnectionPool {

  protected val socketConnections :
    scala.collection.mutable.Map[String, ActiveControl]

  def put(id: String, socket: ActiveControl)

  def get(id: String): ActiveControl

  def closeIdleConnection() : Int

  def closeAllConnection() : Unit
}
