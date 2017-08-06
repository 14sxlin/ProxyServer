package http

import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/6.
  */
class TestRequestLineParser extends FunSuite{

  private val requestLine = "GET /http/http-tutorial.html HTTP/1.1"
  private val wrongLine = "a_line_without_two_space"
  test(" test parse "){
    val (method,uri,version) = HttpRequestLineParser.parse(requestLine)
    assert(method=="GET"
      && uri=="/http/http-tutorial.html"
      && version=="HTTP/1.1")

    assertThrows[IllegalArgumentException](HttpRequestLineParser.parse(wrongLine))
  }
}
