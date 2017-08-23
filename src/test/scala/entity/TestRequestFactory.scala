package entity

import entity.request.RequestFactory
import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/19.
  */
class TestRequestFactory extends FunSuite {


  test("buildRequest") {

    val emptyBodyRequest = Array(
      "GET http://localhost:9000/ HTTP/1.1\nHost: localhost:9000\n",
      "GET http://localhost:9000/ HTTP/1.1\r\nHost: localhost:9000\r\n",
      "GET http://localhost:9000/ HTTP/1.1\r\nHost: localhost:9000\n",
      "GET http://localhost:9000/ HTTP/1.1\n\n\n"
    )

    val bodyRequest = Array(
      "GET http://localhost:9000/ HTTP/1.1\nHost: localhost:9000\n\nbody\n",
      "GET http://localhost:9000/ HTTP/1.1\r\nHost: localhost:9000\r\n\r\nbody\r\n"
    )
    for (rawData <- emptyBodyRequest) {
      println(rawData)
      val request = RequestFactory.buildRequest(rawData)
      assert(request.body.isEmpty)
    }

    for (rawData <- bodyRequest) {
      println(rawData)
      val request = RequestFactory.buildRequest(rawData)
      assert(!request.body.isEmpty)
    }
  }

  test("trim \r\n"){
    val data = "\r\n\r\n"
    assert(data.trim == "")
  }
}
