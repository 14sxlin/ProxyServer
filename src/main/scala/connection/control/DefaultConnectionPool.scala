package connection.control

import constants.LoggerMark
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created by linsixin on 2017/8/12.
  */
class DefaultConnectionPool extends ConnectionPool {

  private val logger = LoggerFactory.getLogger(getClass)

  override val socketConnections: mutable.Map[String, ActiveControl]
  = mutable.Map[String, ActiveControl]()

  override def put(id: String, socketCon: ActiveControl): Unit = {
    socketConnections += (id -> socketCon)
  }

  override def get(id: String): ActiveControl = {
    socketConnections(id)
  }

  override def closeIdleConnection(): Int = {
    var count = 0
    socketConnections.values.foreach { con =>
      if (con.isIdle) {
        logger.info(s"close ${con.toString}")
        con.timeToClose()
        count += 1
      }
    }
    logger.info(s"${LoggerMark.resource} collect $count idle connection")
    count
  }

  override def closeAllConnection(): Unit = {
    socketConnections.values.foreach { con =>
      con.timeToClose()
    }
    logger.info(s"${LoggerMark.resource} close all connection")
  }
}
