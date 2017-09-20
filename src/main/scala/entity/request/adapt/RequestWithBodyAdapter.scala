package entity.request.adapt

import entity.request._
import org.apache.http.client.methods.{HttpPost, HttpUriRequest}
import org.apache.http.entity.{ByteArrayEntity, StringEntity}

/**
  * Created by linsixin on 2017/8/19.
  * This class will transform Request to HttpUriRequest
  * that will use in HttpClient.
  */
object RequestWithBodyAdapter extends RequestAdapter {

  override def adapt(request: Request): HttpUriRequest = {
    request match {
      case EmptyRequest  =>
        throw new IllegalArgumentException("empty able")
      case _:TotalEncryptRequest =>
        throw new IllegalArgumentException("total encrypt able shouldn't process here")
      case _: EmptyBodyRequest =>
        throw new IllegalArgumentException("body empty able shouldn't process here")
      case request: TextRequest =>
        val httpPost = new HttpPost(request.uri)
        putHeaders(request, httpPost)
        putStringEntityToPost(request.body,httpPost)
        httpPost
      case request: ByteBodyRequest =>
        val httpPost = new HttpPost(request.uri)
        putHeaders(request, httpPost)
        putBinaryToPost(request.body,httpPost)
        httpPost
    }

  }

  def putStringEntityToPost(data: String,
                            post: HttpPost): Unit = {
    val string = new StringEntity(data)
    string.setChunked(true)
    post.setEntity(string)
  }

  def putBinaryToPost(data:Array[Byte], post: HttpPost):Unit = {
    val binary = new ByteArrayEntity(data)
    binary.setChunked(true)
    post.setEntity(binary)
  }
}
