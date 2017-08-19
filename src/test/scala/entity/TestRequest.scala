package entity

import entity.request.Request
import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/12.
  */
class TestRequest extends FunSuite{
  val req1 = Request("GET www.baidu.com http/1.1",
    Array(
      ("Host","localhost"),
      ("Content-Type","html/text")
    ),
    "body")
  val expLength = 5

  test("test request to string"){
    assert(req1.mkHttpString.split("\r\n").length == expLength)
  }
}
