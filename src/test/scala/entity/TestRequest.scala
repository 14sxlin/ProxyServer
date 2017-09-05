package entity

import entity.request.TextRequest
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/8/12.
  */
class TestRequest extends FunSuite{


  test("test session to string"){
    val req1 = TextRequest("GET www.baidu.com http/1.1",
      Array(
        ("Host","localhost"),
        ("Content-Type","html/text")
      ),
      "body")
    val expLength = 5
    assert(req1.mkHttpString().split("\r\n").length == expLength)
  }

  test("get encoding"){
    val r1 = TextRequest("",
      Array(("Content-Type","asdjbfas; charset=utf8")),
      "")
    assert(r1.getContentEncoding.contains("utf8"))

    val r2 = TextRequest("",
      Array(("Content-Length","asjdf")),
      "")
    assert(r2.getContentEncoding.isEmpty)

    val r3 = TextRequest("",
      Array(("Content-Type","asdjbfas")),
      "")
    assert(r3.getContentEncoding.isEmpty)

  }


}
