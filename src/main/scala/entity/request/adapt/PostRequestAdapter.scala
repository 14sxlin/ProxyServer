package entity.request.adapt

import entity.body.{EmptyBody, FormParams, TextPlain}
import entity.request.Request
import entity.request.wrapped.WrappedRequest
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpPost, HttpUriRequest}
import org.apache.http.entity.{ByteArrayEntity, StringEntity}
import org.apache.http.message.BasicNameValuePair

import scala.collection.JavaConversions._

/**
  * Created by linsixin on 2017/8/19.
  */
object PostRequestAdapter extends RequestAdapter {

  override def adapt(request: Request): HttpUriRequest = {
    val httpPost = new HttpPost(request.firstLineInfo._2)

    putHeaders(request, httpPost)
    putBodyToPost(request, httpPost)

    httpPost
  }

  def putBodyToPost(request: Request, post: HttpPost): Unit = {
    request match {
      case wrappedRequest: WrappedRequest =>
        putBodyEntityToPost(wrappedRequest, post)

      case _ if !request.body.isEmpty =>
        putBinaryToPost(request.body.getBytes(), post)

    }
  }

  def putBodyEntityToPost(wrappedRequest: WrappedRequest,
                          post: HttpPost): Unit = {
    wrappedRequest.bodyEntity match {
      case EmptyBody => ()

      case text: TextPlain =>
        putStringEntityToPost(text.data, post)

      case form: FormParams =>
        putFormParamToPost(form, post)

      case _ =>
        putBinaryToPost(wrappedRequest.rawBody.getBytes, post)
    }
  }

  def putStringEntityToPost(data: String,
                            post: HttpPost): Unit = {
    val string = new StringEntity(data)
    string.setChunked(true)
    post.setEntity(string)
  }

  def putFormParamToPost(form: FormParams,
                         post: HttpPost): Unit = {
    val nameValuePairs = for ((name, value) <- form.params)
      yield new BasicNameValuePair(name, value)
    val formParams = new UrlEncodedFormEntity(nameValuePairs.toList)
    formParams.setChunked(true)
    post.setEntity(formParams)
  }

  def putBinaryToPost(data:Array[Byte], post: HttpPost):Unit = {
    val binary = new ByteArrayEntity(data)
    binary.setChunked(true)
    post.setEntity(binary)
  }
}
