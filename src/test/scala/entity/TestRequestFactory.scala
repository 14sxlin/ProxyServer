package entity

import entity.request.RequestFactory
import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/19.
  */
class TestRequestFactory extends FunSuite {


  test("buildRequest about body") {

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

  test("test bug : buildRequest"){
    val raw = "POST http://qurl.f.360.cn/wdinfo.php HTTP/1.1\nHost: qurl.f.360.cn\nAccept: */*\nProxy-Connection: Keep-Alive\nCache-Control: no-cache\nContent-Type: application/octet-stream\nContent-Length: 246\n\n\u001E\n�o�U��q�e\"\u0006�d~*�u�;�P\u007Fj7\u0014�g�T���GZ�L��\u05FFm\u0005�ha�\u0002�ʺES�05\"�\uEBE5��\u001C_�<`�\u0006�6\u0014҅�r��a�E����u�!�)�c[���_aHY�:�kZ��~B�@��Û\u000616bs����;�[�oD��T�#���x�f&Uz���C�b�\u0014`�"
    RequestFactory.buildRequest(raw)
  }
}
