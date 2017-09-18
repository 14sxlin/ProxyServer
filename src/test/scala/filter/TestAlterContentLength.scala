package filter

import entity.response.TextResponse
import filter.response.ResponseContentLengthFilter
import org.apache.http.HttpHeaders
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/9/17.
  */
class TestAlterContentLength extends FunSuite{

  test("alteContentLength"){
    val headers = Array(
      ("Host","shdfoisdf"),
      (HttpHeaders.CONTENT_LENGTH,"38"),
      ("Other-Header","sdnfksdjnf")
    )
    val response = new TextResponse("",headers,"123")
    val newResponse = ResponseContentLengthFilter.handle(response)
    println(newResponse.mkHttpString())
    assert(newResponse.headers.count( _._1 == HttpHeaders.CONTENT_LENGTH) == 1)
  }
}
