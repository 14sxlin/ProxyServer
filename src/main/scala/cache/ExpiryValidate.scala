package cache

/**
  * Created by linsixin on 2017/9/15.
  */
class ExpiryValidate extends Validate {

  var expires : String = ""

  def this(expires:String) = {
    this()
    this.expires = expires
  }
  override def isOutOfDate = ???
}
