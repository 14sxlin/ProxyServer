package connection.control
import connection.ClientServiceUnit
/**
  * Created by linsixin on 2017/8/30.
  */
class ClientServicePool extends ServiceUnitPool[ClientServiceUnit]{

  override def put(key: String, unit: ClientServiceUnit): Unit = {
    unit.updateActiveTime()
    map.put(key,unit)
  }

  override def get(key: String): Option[ClientServiceUnit] = {
    if(containsKey(key))
    {
      val unit = map.get(key)
      unit.updateActiveTime()
      Some(unit)
    }
    else None
  }

}
