package httpclient

import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.HttpClients
import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/14.
  */
class TestHttpSecurity extends FunSuite {
  test("") {
    val client = HttpClients.createDefault()
    val defaultCred = new UsernamePasswordCredentials("sparrowxin", "123456")


  }
}
