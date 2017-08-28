package entity

import entity.response.Response
import filter.response.ResponseContentLengthFilter
import org.apache.http.HttpHeaders
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/8/27.
  */
class TestResponseContentLength extends FunSuite{

  val contentLength = HttpHeaders.CONTENT_LENGTH

  test("content length"){
    val headers = Array(
      ("Host","uuuuuuuuu"),
      ("Content-Type","asdjfbskadjf")
    )
    val body = "1234456"
    val response = Response("##########",headers,body)
    val newResponse = ResponseContentLengthFilter.handle(response)
    val newHeaders = newResponse.headers
    assert(newHeaders.contains((contentLength,""+body.length)))

  }

  /**
    * conclude that :
    * length of bytes in English and string is equals because ascii
    * length of bytes in Chinese and string is not equals
    * length of bytes in Chinese but in different encoding
    *   such as gbk and utf8 are different
    */
  test("string length will not equal to bytes length"){
    val eng = "this is some english"
    val chi = "这是一些中文字符"

    assert(eng.length == eng.getBytes.length)
    assert(chi.length != chi.getBytes.length)
    assert(chi.getBytes("gbk").length != chi.getBytes("utf8").length)

  }
}
