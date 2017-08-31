package filter

import entity.response.TextResponse
import org.apache.http.HttpHeaders
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/8/31.
  */
class TestResponseChain extends FunSuite{

  test("response chain "){
    val response = TextResponse(
      "*********",
      Array(
        (HttpHeaders.CONNECTION,"close"),
        (HttpHeaders.TRANSFER_ENCODING,"chunked")
      ),
      "1234567890"
    )
    val newResponse = ResponseFilterChain.handle(response)
    assert(newResponse.headers.contains((HttpHeaders.CONTENT_LENGTH,"10")))
    assert(!newResponse.headers.exists(nv => nv._1 == HttpHeaders.TRANSFER_ENCODING))
    assert(newResponse.connectionCloseFlag)

  }
}
