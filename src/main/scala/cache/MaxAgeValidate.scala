package cache

import java.time.LocalDateTime

/**
  * Created by linsixin on 2017/9/14.
  */
class MaxAgeValidate extends Validate{

  var maxAge :String = ""

  def this(maxAge:String) = {
    this()
    this.maxAge = maxAge
  }


  override def prolongDelay(): Unit = updateCreateTime()

  override def fill(maxAge: String): Unit = {
    this.maxAge = maxAge
  }

  override def isOutOfDate : Boolean = {
    if(maxAge== null || maxAge.isEmpty)
      true
    else {
      try {
        val dateTime = createTime.plusSeconds(maxAge.toLong)
        LocalDateTime.now().isAfter(dateTime)
      }catch {
        case  e:NumberFormatException =>
          e.printStackTrace()
          true
        case e:Exception =>
          e.printStackTrace()
          true
      }
    }

  }
}
