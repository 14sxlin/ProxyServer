package cache

import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/9/14.
  */
class TestCache extends FunSuite{
  test("disk cache "){
    val cache = new HttpCache("D://test-cache/1.temp")
    cache.put("hello","hello".getBytes())

    val data = cache.get("hello").get
    assert("hello" == new String(data))

    cache.replace("hello","hello world".getBytes())
    val newData = cache.get("hello").get
    assert("hello world" == new String(newData))

    cache.close()
  }

}
