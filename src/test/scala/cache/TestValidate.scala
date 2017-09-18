package cache

import java.time.{LocalDateTime, ZonedDateTime}
import java.time.format.DateTimeFormatter

import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/9/16.
  */
class TestValidate extends FunSuite {

  test("max age should not out of date") {
    val validate = new MaxAgeValidate("10")
    assert(!validate.isOutOfDate)
  }

  test("expiry should not out of date"){
    val expiry = ZonedDateTime.now().plusSeconds(2L).format(DateTimeFormatter.RFC_1123_DATE_TIME)
    println(s"expire: $expiry")
    println(s"rule : ${ZonedDateTime.now().getZone.getRules}")
    val validate = new ExpiryValidate(expiry)
    assert(!validate.isOutOfDate)
  }

  test("expiry should not out of date end with GMT"){
    val expiry = "Tue, 28 Aug 2018 10:13:19 GMT"
    val validate = new ExpiryValidate(expiry)
    assert(! validate.isOutOfDate)
  }

}
