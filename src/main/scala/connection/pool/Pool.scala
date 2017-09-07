package connection.pool

import java.util.concurrent.ConcurrentHashMap

/**
  * Created by linsixin on 2017/9/7.
  */
trait Pool[T] {

  protected val map = new ConcurrentHashMap[String, T]()

  def containsKey(key:String) : Boolean = {
    map.containsKey(key)
  }

  def put(key: String, elem: T): Unit ={
    map.put(key,elem)
  }

  def get(key: String): Option[T] = {
    if(containsKey(key))
    {
      val unit = map.get(key)
      Some(unit)
    }
    else None
  }

  def remove(key:String):Unit = {
    if(map.containsKey(key)){
      map.remove(key)
    }
  }
}
