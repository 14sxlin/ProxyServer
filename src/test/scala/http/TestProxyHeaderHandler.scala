package http

import handler.header.ProxyHeaderHandler
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

  val handler = new ProxyHeaderHandler

  test("test handle filter one header"){
    val result = handler.handle(headers1)

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
  test("test handle filter more header"){
    val result = handler.handle(headers2)

    assert(result.length == 1 &&
      result(0)._1 == "Keep-Alive" &&
      result(0)._2 == "lalal"
    )
  }
}
