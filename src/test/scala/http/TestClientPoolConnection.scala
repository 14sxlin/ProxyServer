package http

import java.util.concurrent.ArrayBlockingQueue

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import org.scalatest.{FunSuite, Ignore}

/**
  * Created by sparr on 2017/8/25.
  */
class TestClientPoolConnection extends FunSuite{
  test("test get localhost"){
    val httpUri = new HttpGet("http://localhost:8080/LoginDemo")
    val context = HttpClientContext.create()
    val poolingClient = new ConnectionPoolingClient
    assert(poolingClient.doRequest(httpUri,context).body.length!=0)

    val picGet = new HttpGet("http://localhost:8080/LoginDemo/pic/1.jpg")
    assert(poolingClient.doRequest(picGet,context).body.length!=0)
  }

//  test("what will happen if queue empty and try to take"){
//    val queue = new ArrayBlockingQueue[String](5)
//    queue.take() // It will block
//
//  }
}
