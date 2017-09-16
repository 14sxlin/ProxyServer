package cache

import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/9/14.
  */
class TestCache extends FunSuite{
  test("disk cache "){
    val cache = new HttpCache("D://test-cache/1.temp")
    cache.put("hello",CacheUnit("hello",null))

    val data = cache.get("hello").get
    assert("hello" == data.absoluteUri)

    cache.replace("hello",CacheUnit("hello world",null))
    val newData = cache.get("hello").get
    assert("hello world" == newData.absoluteUri)

    cache.close()
  }

}
