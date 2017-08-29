package entity.request.dispatch

import java.util.concurrent.ConcurrentHashMap

import constants.LoggerMark
import org.apache.http.client.methods.HttpUriRequest
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/25.
  */
object RequestDispatcher {

  private val logger = LoggerFactory.getLogger(getClass)


  private val map = new ConcurrentHashMap[String, RequestSession]()

  /**
    *
    * @param key     a string represent the same connection
    *                It's "{client socket's address}:{client's port}-{Server's uri}"
    *                @see HashUtils
    * @param request HttpClient uri request
    * @return if there already has this hash
    */
  def dispatch(key: String,
               request: HttpUriRequest): Boolean = {
    if (map.containsKey(key)) {
      val session = map.get(key)
      session.put(request)
      logger.info(s"${LoggerMark.resource} has Maps: ${map.size()}")
      true
    }else{
      val newSession = new RequestSession(key)
      newSession.put(request)
      map.put(key, newSession)
      logger.info(s"${LoggerMark.resource} has Maps: ${map.size()}")
      false
    }
  }

  def getRequestSession(key: String): Option[RequestSession] = {
    if (map.containsKey(key))
      Some(map.get(key))
    else
      None
  }

  def upDispatch(key:String):Unit = {
    if(map.containsKey(key))
      map.remove(key)
  }



}
