package filter

import java.net.URLEncoder

import entity.request.Request
import filter.request.InvalidUrlCharacterFilter
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/8/31.
  */
class TestInvalidUrlCharacterFilter extends FunSuite{

  val uri = "http://pmm.people.com.cn/main/s?user=people|2016people|textlink03&db=people&border=0&local=yes&js=ie"

  test("get with query string"){

    val encodedUri = uri.replace("|",URLEncoder.encode("|","utf-8"))
//    println(encodedUri)
    //    println(uri.substring(43))
    val httpGet = new HttpGet(encodedUri)
    val client = HttpClients.createDefault()
    val res = client.execute(httpGet)
    assert(res.getStatusLine.toString != null)
    println(EntityUtils.toString(res.getEntity))
    res.close()
    client.close()
  }

  test("test filter | "){
    val firstLine = "GET "+uri+" HTTP/1.1"
    val requestWithInvalidUri  = Request(firstLine,Array.empty,"")
    val newRequest = InvalidUrlCharacterFilter.handle(requestWithInvalidUri)
    val encodedUri = uri.replace("|",URLEncoder.encode("|","utf-8"))
    assert(newRequest.uri == encodedUri)
  }
}
