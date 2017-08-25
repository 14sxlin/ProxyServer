package entity

import entity.request.Request
import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/12.
  */
class TestRequest extends FunSuite{


  test("test session to string"){
    val req1 = Request("GET www.baidu.com http/1.1",
      Array(
        ("Host","localhost"),
        ("Content-Type","html/text")
      ),
      "body")
    val expLength = 5
    assert(req1.mkHttpString.split("\r\n").length == expLength)
  }

  test("get encoding"){
    val r1 = Request("",
      Array(("Content-Type","asdjbfas; charset=utf8")),
      "")
    assert(r1.getContentEncoding.contains("utf8"))

    val r2 = Request("",
      Array(("Content-Length","asjdf")),
      "")
    assert(r2.getContentEncoding.isEmpty)

    val r3 = Request("",
      Array(("Content-Type","asdjbfas")),
      "")
    assert(r3.getContentEncoding.isEmpty)

  }


}
