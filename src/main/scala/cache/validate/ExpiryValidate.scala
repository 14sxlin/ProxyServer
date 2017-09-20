package cache.validate

import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.{LocalDateTime, ZonedDateTime}

/**
  * Created by linsixin on 2017/9/15.
  */
class ExpiryValidate extends Validate {

  var expires : String = ""

  def this(expires:String) = {
    this()
    this.expires = expires
  }

  override def prolongDelay(): Unit = {
    expires = ZonedDateTime.now().format(
      DateTimeFormatter.RFC_1123_DATE_TIME)
  }

  override def fill(expires: String): Unit = {
    this.expires = expires
  }

  override def isOutOfDate:Boolean = {
    if(expires==null || expires.isEmpty)
      true
    else try{
      LocalDateTime.now().isAfter(LocalDateTime.parse(expires,
        DateTimeFormatter.RFC_1123_DATE_TIME))
    }catch {
      case _ : DateTimeParseException =>
        true
      case e : Exception =>
        e.printStackTrace()
        true
    }
  }
}
