package cache

/**
  * Created by linsixin on 2017/9/14.
  */
trait Cacheable extends Serializable{

  var validate : Validate = _

  def hasValidateMechanism : Boolean = validate != null

  def prolong():Unit = {
    if(!hasValidateMechanism)
      throw new IllegalStateException("no validate machanism")
    else
      validate.prolongDelay()
  }

}
