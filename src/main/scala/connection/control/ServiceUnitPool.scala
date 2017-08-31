package connection.control

import java.util.concurrent.ConcurrentHashMap

import connection.ServiceUnit

/**
  * Created by linsixin on 2017/8/11.
  */
trait ServiceUnitPool[T <: ServiceUnit] {

  protected val map = new ConcurrentHashMap[String, T]()

  def put(key: String, unit: T)

  def get(key: String): Option[T]

  def remove(key:String):Unit = {
    if(map.containsKey(key)){
      map.remove(key)
    }
  }

  def containsKey(key:String) : Boolean = {
    map.containsKey(key)
  }

  def removeIdleServiceUnit() : Int = {
    val keys = map.keys()
    var count = 0
    while(keys.hasMoreElements){
      val key = keys.nextElement()
      val e = map.get(key)
      if(e.isIdle){
        count += 1
        e.closeWhenNotActive()
        map.remove(key)
      }
    }
    count
  }

}
