import entity.response.{BinaryResponse, TextResponse}
import filter.ResponseFilterChain
import http.{ConnectionPoolClient, RequestProxy}
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
//    "http://g.alicdn.com/browser/uc123_no/0.4.9/index/js/weather.min.js",
//    "http://g.alicdn.com/browser/uc123_no/0.4.9/index/css/uc.home.pkg.min.css"
//    "http://g.alicdn.com/pecdn/mlog/agp_heat.min.js?t=209010",
//    "http://www.uc123.com/?f=pcntgrid",
    "http://i-7.vcimg.com/crop/80b4b084d7df94f08aab46998921024a42433(100x100)/thumb.jpg"
  )

  val pool = new ConnectionPoolClient
  val requestProxy = new RequestProxy(pool)
  val context = HttpClientContext.create()
  var count = 0
  for(url <- failUrls){
    val get = new HttpGet(url)
    val response = requestProxy.doRequest(get,context)
    response match {
      case r : TextResponse =>
        println("text response")
        println(ResponseFilterChain.handle(r).mkHttpString())
      case r : BinaryResponse =>
        println("binary response")
        FileUtils.save2File(s"logs/pic/test.jpg",r.body)
        println(ResponseFilterChain.handle(r).mkHttpString())

    }

//    val response1 = HttpUtils.execute(get)
//    println(response1.mkHttpString())
    count += 1
  }
}
