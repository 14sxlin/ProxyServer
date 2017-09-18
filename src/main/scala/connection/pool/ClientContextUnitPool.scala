package connection.pool

import model.ContextUnit

/**
  * Created by linsixin on 2017/8/30.
  */
class ClientContextUnitPool extends ContextUnitPool[ContextUnit]{

  override def put(key: String, unit: ContextUnit): Unit = {
    unit.updateActiveTime()
    map.put(key,unit)
  }

  override def get(key: String): Option[ContextUnit] = {
    if(containsKey(key))
    {
      val unit = map.get(key)
      Some(unit)
    }
    else None
  }

}
