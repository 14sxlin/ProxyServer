package http

import exception.NotHeaderException
import org.scalatest.FunSuite


/**
  * Created by sparr on 2017/7/30.
  */
class TestHttpProxyServer extends FunSuite{

  val server = new HttpProxyServer
//
//  test("test parse header"){
//    val validHeader = "Host: mmstat.ucweb.com:443"
//    val keyValues = server.
//
//    val invalidHeader = "not a header"
//    assertThrows[NotHeaderException](server.parseHeader(invalidHeader))
//
//  }


}
