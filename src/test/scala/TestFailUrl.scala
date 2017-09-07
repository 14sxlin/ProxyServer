import entity.response.{BinaryResponse, TextResponse}
import http.{ConnectionPoolingClient, RequestProxy}
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import utils.FileUtils

/**
  * Created by linsixin on 2017/9/7.
  */
object TestFailUrl extends App{

  val failUrls = Array(
//    "http://cn.chinadaily.com.cn/img/attachement/jpg/site1/20170907/448a5bd66d0d1b1b6ce61e.jpg",
//    "http://cn.chinadaily.com.cn/img/attachement/jpg/site1/20170907/448a5bd66b851b1b385a02.jpg",
//    "http://cn.chinadaily.com.cn/img/attachement/jpg/site1/20170907/d4bed9d4d2201b1b3eae2c.jpg",
//    "http://same.chinadaily.com.cn/s?z=chinadaily&c=2321",
//    "http://same.chinadaily.com.cn/s?z=chinadaily&c=2322",
//    "http://images.china.cn/images1/ch/2015china/phone.js",
    "http://g.alicdn.com/browser/uc123_no/0.4.9/index/js/weather.min.js"
  )

  val pool = new ConnectionPoolingClient
  val requestProxy = new RequestProxy(pool)
  val context = HttpClientContext.create()
  var count = 0
  for(url <- failUrls){
    val response = requestProxy.doRequest(new HttpGet(url),context)
    response match {
      case r : TextResponse =>
        println(r.mkHttpString())
      case r : BinaryResponse =>
        FileUtils.save2File(s"logs/pic/$count.jpg",r.body)
        println(r.mkHttpString())
    }
    count += 1
  }
}
