package mock.client

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import utils.http.HttpUtils

/**
  * Created by sparr on 2017/8/20.
  */
object HttpsClientDemo extends App{

  val httpsGet = new HttpGet("http://www.autohome.com.cn/beijing/")
  val response = HttpUtils.execute(httpsGet)

  println(response.mkHttpString)
}
