package cache

import scala.beans.BeanProperty

/**
  * Created by linsixin on 2017/9/14.
  */
trait Cacheable {

  @BeanProperty  var validate : Validate = _

  def hasValidateMechanism : Boolean = validate != null
}
