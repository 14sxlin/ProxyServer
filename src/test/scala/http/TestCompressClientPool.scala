package http

import entity.response.BinaryResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/9/9.
  */
class TestCompressClientPool extends FunSuite{

  test("test compress"){
    val httpUri = new HttpGet("http://www.uc123.com/")
    val context = HttpClientContext.create()
    val poolingClient = new ConnectionPoolClient
    val response = poolingClient.doRequest(httpUri,context).asInstanceOf[BinaryResponse]
    println("uncompress -------------------: \n" + response.getHeadersString)
    val compressClient = new CompressConnectionPoolClient
    val compressResponse = compressClient.doRequest(httpUri,context).asInstanceOf[BinaryResponse]
    println("compress ---------------------: \n" + compressResponse.getHeadersString)
    assert(response.body.length > compressResponse.body.length)
  }
}
