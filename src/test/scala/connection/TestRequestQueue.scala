package connection

import java.util.concurrent.ArrayBlockingQueue

import entity.request.ByteBodyRequest
import org.apache.http.client.methods.HttpGet
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/8/27.
  */
class TestRequestQueue extends FunSuite{

  test("test take if empty queue"){
    val queue = new ArrayBlockingQueue[String](20)
    val consumerRun = new Runnable {
      override def run(): Unit = {
        while(true){
          val str = queue.take()
          print(s"take $str")
          // so strange that it won't output until you stop
          // the progress
        }
      }
    }
    val providerRun = new Runnable {
      override def run(): Unit = {
        while(true){
          queue.put(""+Math.random())
        }
      }
    }

    val consumerThread1 = new Thread(consumerRun);consumerThread1.start()
    val consumerThread2 = new Thread(consumerRun);consumerThread2.start()
    val proThread = new Thread(providerRun);proThread.start()

    consumerThread1.join()
    proThread.join()
  }

}
