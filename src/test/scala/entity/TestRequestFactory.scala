package entity

import entity.request.{ByteBodyRequest, EmptyBodyRequest, HeaderRecognizedRequest, RequestFactory}
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/8/19.
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
      val request = RequestFactory.buildRequest(rawData.getBytes("utf8"))
      request match {
        case r:HeaderRecognizedRequest =>
          assert(r.isInstanceOf[EmptyBodyRequest])
      }
    }

    for (rawData <- bodyRequest) {
      println(rawData)
      val request = RequestFactory.buildRequest(rawData.getBytes("utf8"))
      request match {
        case r:HeaderRecognizedRequest =>
          assert(r.isInstanceOf[ByteBodyRequest])
      }
    }
  }

  test("trim \r\n"){
    val data = "\r\n\r\n"
    assert(data.trim == "")
  }

  test("test bug : buildRequest"){
    val raw = "POST http://qurl.f.360.cn/wdinfo.php HTTP/1.1\nHost: qurl.f.360.cn\nAccept: */*\nProxy-Connection: Keep-Alive\nCache-Control: no-cache\nContent-Type: application/octet-stream\nContent-Length: 246\n\n\u001E\n�o�U��q�e\"\u0006�d~*�u�;�P\u007Fj7\u0014�g�T���GZ�L��\u05FFm\u0005�ha�\u0002�ʺES�05\"�\uEBE5��\u001C_�<`�\u0006�6\u0014҅�r��a�E����u�!�)�c[���_aHY�:�kZ��~B�@��Û\u000616bs����;�[�oD��T�#���x�f&Uz���C�b�\u0014`�"
    val request = RequestFactory.buildRequest(raw.getBytes)
    request match {
      case r:HeaderRecognizedRequest =>
        assert(r.isInstanceOf[ByteBodyRequest])
    }
  }


  test("test build able from bytes"){
    val firstLine = "POST http://qurl.f.360.cn/wdinfo.php HTTP/1.1\n"
    val headers =
      "Host: qurl.f.360.cn\n" +
        "Accept: */*\n" +
        "Proxy-Connection: Keep-Alive\n" +
        "Cache-Control: no-cache\n"
    val body = "this is body content"
    val rawRequest =
      firstLine +
        headers +
        "\n" +
        body

    val bytes = rawRequest.getBytes
    val request = RequestFactory.buildRequest(bytes).asInstanceOf[ByteBodyRequest]
    assert(request.firstLine == firstLine.trim)
    for(header <- request.headers)
      println(s"${header._1} : ${header._2}")
    assert(request.toTextRequest().body == body)
  }

  test("should not format bytes to string until we know it's text/html "){
    val firstLine = "POST http://qurl.f.360.cn/wdinfo.php HTTP/1.1\n"
    val headers =
      "Host: qurl.f.360.cn\n" +
      "Accept: */*\n" +
      "Proxy-Connection: Keep-Alive\n" +
      "Cache-Control: no-cache\n"
    val body = "this is body content"
    val rawRequest =
        firstLine +
        headers +
        "\n" +
        body

    val bytes = rawRequest.getBytes()
    val split = "\n".getBytes.head
    assert(bytes.count( _ == split) == 6)

    val LFCR = "\r\n".getBytes()
    LFCR foreach print

  }

  test("end with \r\n rather than \n"){
    val text = "GET http://localhost:9000/ HTTP/1.1\r\nHost: localhost:9000\r\n\r\nthis is body part"
    val bytes = text.getBytes()
    val request = RequestFactory.buildRequest(bytes).asInstanceOf[ByteBodyRequest]
    assert(request.firstLine == "GET http://localhost:9000/ HTTP/1.1".trim)
    assert(request.headers.length==1
      && request.headers(0)._1=="Host"
      && request.headers(0)._2=="localhost:9000")
    assert(request.toTextRequest().body == "this is body part")
  }

  test("body is CRLF"){
    val text = "GET http://localhost:9000/ HTTP/1.1\r\nHost: localhost:9000\r\n\r\n\r\n"
    val bytes = text.getBytes()
    val request = RequestFactory.buildRequest(bytes).asInstanceOf[ByteBodyRequest]
    assert(request.firstLine == "GET http://localhost:9000/ HTTP/1.1".trim)
    assert(request.headers.length==1
      && request.headers(0)._1=="Host"
      && request.headers(0)._2=="localhost:9000")
    assert(request.toTextRequest().body == "\r\n")
  }

  test("body is LF"){
    val text = "GET http://localhost:9000/ HTTP/1.1\r\nHost: localhost:9000\r\n\r\n\n"
    val bytes = text.getBytes()
    val request = RequestFactory.buildRequest(bytes).asInstanceOf[ByteBodyRequest]
    assert(request.firstLine == "GET http://localhost:9000/ HTTP/1.1".trim)
    assert(request.headers.length==1
      && request.headers(0)._1=="Host"
      && request.headers(0)._2=="localhost:9000")
    assert(request.toTextRequest().body == "\n")
  }


}
