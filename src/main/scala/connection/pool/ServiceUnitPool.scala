package connection.pool

import connection.ServiceUnit

/**
  * Created by linsixin on 2017/8/11.
  */
trait ServiceUnitPool[T <: ServiceUnit] extends Pool[T]{

  def closeAndRemove(key:String):Unit = {
    if(map.containsKey(key)){
      map.get(key).closeWhenNotActive()
      remove(key)
    }
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
