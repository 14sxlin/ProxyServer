package entity.request.dispatch

import java.util.concurrent.ConcurrentHashMap

import org.apache.http.client.methods.HttpUriRequest

/**
  * Created by linsixin on 2017/8/25.
  */
object RequestDispatcher {

  private val map = new ConcurrentHashMap[String, RequestSession]()

  /**
    *
    * @param key     a string represent the same connection
    *                It's "{client socket's address}:{client's port}:{Server's uri}"
    * @param request HttpClient uri request
    * @return if there already has this hash
    */
  def dispatch(key: String,
               request: HttpUriRequest): Boolean = {
    if (map.contains(key)) {
      val session = map.get(key)
      session.put(request)
      true
    }else{
      val newSession = new RequestSession(key)
      newSession.put(request)
      map.put(key, newSession)
      false
    }
  }

  def getRequestSession(key: String): Option[RequestSession] = {
    if (map.containsKey(key))
      Some(map.get(key))
    else
      None
  }



}
