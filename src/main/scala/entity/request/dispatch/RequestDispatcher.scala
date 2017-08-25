package entity.request.dispatch

import java.util.concurrent.ConcurrentHashMap

import entity.request.Request
import org.apache.http.client.methods.HttpUriRequest

import scala.collection.mutable

/**
  * Created by linsixin on 2017/8/25.
  */
object RequestDispatcher {

  private val map = new ConcurrentHashMap[String, RequestSession]()

  def dispatch(hash:String,
               request:HttpUriRequest):RequestSession = {
    if(map.contains(hash)){
      val session = map.get(hash)
      session.put(request)
      session
    }else{
      val newSession = new RequestSession(hash)
      newSession.put(request)
      map.put(hash,newSession)
      newSession
    }
  }



}
