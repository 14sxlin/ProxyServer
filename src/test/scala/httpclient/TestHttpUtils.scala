package httpclient

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/8/19.
  */
class TestHttpUtils extends FunSuite {
  test("get") {
    val httpGet = new HttpGet("http://localhost:9000")
    val client = HttpClients.createDefault()
    val res = client.execute(httpGet)
    assert(res.getStatusLine.toString != null)
    res.close()
    client.close()
  }
}
