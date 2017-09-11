package http

import java.util.concurrent.ArrayBlockingQueue

import entity.response.{BinaryResponse, TextResponse}
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import org.scalatest.{FunSuite, Ignore}
import utils.HexUtils

/**
  * Created by linsixin on 2017/8/25.
  */
class TestClientPoolConnection extends FunSuite{
  test("test get localhost"){
    val httpUri = new HttpGet("http://localhost:8080/LoginDemo")
    val context = HttpClientContext.create()
    val poolingClient = new ConnectionPoolClient
    poolingClient.doRequest(httpUri,context) match {
      case r:TextResponse =>
        println(r.mkHttpString())
        assert(r.body.length!=0)
      case r:BinaryResponse =>
        println(r.mkHttpString())
        assert(r.body.length!=0)
    }

    val picGet = new HttpGet("http://localhost:8080/LoginDemo/pic/1.jpg")
    poolingClient.doRequest(picGet,context) match {
      case r:TextResponse =>
        println(r.mkHttpString())
        assert(r.body.length!=0)
      case r:BinaryResponse =>
        println(r.mkHttpString())
        assert(r.body.length!=0)
    }

  }

//  test("what will happen if queue empty and try to take"){
//    val queue = new ArrayBlockingQueue[String](5)
//    queue.take() // It will block
//
//  }
}
