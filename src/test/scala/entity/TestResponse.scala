package entity

import entity.response.Response
import org.apache.commons.lang3.StringUtils
import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/23.
  */
class TestResponse extends FunSuite{

  test("mkHttpString "){
    val headers = Array(("A","valueA"),("B","ValueB"))
    val r1 = Response("GET ### ###",headers,"This is body part")
    val s1 = r1.mkHttpString
    assert(s1.split("\r\n").length == 5
      && s1.split("\r\n").indexOf("") == 3)

    val r2 = Response("GET ### ###",headers,"")
    val s2 = r2.mkHttpString
    assert(s2.split("\r\n").length == 3
      && !s2.split("\r\n").contains(""))
  }
}
