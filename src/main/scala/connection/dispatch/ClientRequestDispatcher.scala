package connection.dispatch

import connection.ClientServiceUnit
import connection.control.ClientServicePool
import entity.request.RequestUnit
import org.apache.http.client.methods.HttpUriRequest
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/25.
  */
class ClientRequestDispatcher(pool:ClientServicePool) {

  private val logger = LoggerFactory.getLogger(getClass)

  def addNewServiceUnit(key:String,unit:ClientServiceUnit):Unit = {
    if(!pool.containsKey(key))
      pool.put(key,unit)
    else logger.warn(s"$key has existed in pool")
  }

  def removeExistServiceUnit(key:String):Unit = {
    if(pool.containsKey(key))
      pool.remove(key)
    else logger.warn("try to remove service unit with" +
      s"key $key but it doesn't exist")
  }

  def containsKey(key:String):Boolean = {
    pool.containsKey(key)
  }

  def buildRequestUnit(key:String,
                       request:HttpUriRequest):RequestUnit = {
    pool.get(key) match {
      case None =>
        throw new IllegalArgumentException(s"There must be a ClientServiceUnit whit key $key")
      case Some(serviceUnit) =>
        RequestUnit(
          key,
          request,
          serviceUnit.context,
          ClientServiceUnit.writeResponse(serviceUnit.clientConnection)
        )
    }

  }




}
