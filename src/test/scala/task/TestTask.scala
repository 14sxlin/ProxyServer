package task

import org.apache.http.client.methods.HttpGet
import org.scalatest.FunSuite

/**
  * Created by sparr on 2017/8/20.
  */
class TestTask extends FunSuite {

  test("get task") {
    val httpGet = new HttpGet("http://localhost:9000")
    val task = new OnceGetTask(httpGet)
    task.onSuccess = res => println(res.mkHttpString())
    task.begin()


    Thread.sleep(3000)
  }


}
