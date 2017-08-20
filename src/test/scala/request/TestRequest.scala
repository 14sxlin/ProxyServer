package request

import entity.request.Request
import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/20.
  */
class TestRequest extends FunSuite{

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
