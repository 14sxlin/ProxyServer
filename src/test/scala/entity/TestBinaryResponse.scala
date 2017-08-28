package entity

import org.apache.http.client.methods.HttpGet
import org.scalatest.FunSuite
import utils.http.{FileUtils, HexUtils, HttpUtils, IOUtils}

/**
  * Created by linsixin on 2017/8/28.
  */
class TestBinaryResponse extends FunSuite{
  test("test receive image"){
    val httpGet = new HttpGet("http://localhost:8080/LoginDemo/pic/1.jpg")
    val response = HttpUtils.executeReturnBinary(httpGet)
//    FileUtils.save2File("logs/test/test.jpg",response.body)
    println(response.firstLine)
    response.headers.foreach((nameValue) => println(s"${nameValue._1} ${nameValue._2}"))
    println(response.body.length)
    println(HexUtils.toHex(response.body))
    assert(response != null)
  }
}
