package http

import entity.request.Request
import filter.request.ProxyHeaderFilter
import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/11.
  */
class TestProxyHeaderHandler extends FunSuite{

  val headers1 = Array(
    ("Connection","Host"),
    ("Proxy-Auth","####"),
    ("Host","hasdlf"),
    ("Keep-Alive","lalal")
  )
  val request1 = Request("", headers1, "")

  test("test handle filter one header"){
    val result = ProxyHeaderFilter.handle(request1).headers

    assert(result.length == 1 &&
      result(0)._1 == "Keep-Alive" &&
      result(0)._2 == "lalal"
    )
  }


  val headers2 = Array(
    ("Connection","Host,Content"),
    ("Proxy-Auth","####"),
    ("Host","hasdlf"),
    ("Keep-Alive","lalal"),
    ("Content","")
  )
  val request2 = Request("", headers2, "")
  test("test handle filter more header"){
    val result = ProxyHeaderFilter.handle(request2).headers

    assert(result.length == 1 &&
      result(0)._1 == "Keep-Alive" &&
      result(0)._2 == "lalal"
    )
  }
}
