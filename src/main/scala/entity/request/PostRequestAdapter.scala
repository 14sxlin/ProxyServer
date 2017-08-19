package entity.request

import entity.body.{BodyWrappedRequest, FormParams, TextPlain}
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpPost, HttpUriRequest}
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicNameValuePair

import scala.collection.JavaConversions._

/**
  * Created by sparr on 2017/8/19.
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
      case wrappedRequest: BodyWrappedRequest =>
        putBodyEntityToPost(wrappedRequest, post)

      case _ if !request.body.isEmpty =>
        putStringEntityToPost(request.body, post)

    }
  }

  def putBodyEntityToPost(wrappedRequest: BodyWrappedRequest,
                          post: HttpPost): Unit = {
    wrappedRequest.bodyEntity match {
      case text: TextPlain =>
        putStringEntityToPost(text.data, post)

      case form: FormParams =>
        putFormParamToPost(form, post)

      case _ =>
        putStringEntityToPost(wrappedRequest.rawBody, post)
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
}
