package connection

import entity.request.Request
import entity.request.dispatch.RequestQueue
import org.apache.http.client.methods.HttpGet
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/8/27.
  */
class TestRequestQueue extends FunSuite{

  test("test take if empty queue"){
    val testQueue = new RequestQueue
    val request = new HttpGet("")
    testQueue.put(request)
    while(true){
      testQueue.take()
      print("take success ")
      // so strange that it won't output until you stop
      // the progress
    }

  }

}
