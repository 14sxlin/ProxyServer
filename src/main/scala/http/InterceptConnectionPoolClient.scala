package http

import entity.response.Response
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext

/**
  * Created by linsixin on 2017/9/9.
  */
class InterceptConnectionPoolClient extends ConnectionPoolClient{


  protected def requestIntercept(request:HttpUriRequest,
                                 context: HttpClientContext):HttpUriRequest = {
    request
  }

  protected def responseIntercept(response: HttpResponse,
                                  context: HttpClientContext):HttpResponse = {
    response
  }

  override def doRequest(request:HttpUriRequest,
                           context: HttpClientContext) : Response = {
    val processedRequest = requestIntercept(request,context)
    val httpResponse = client.execute(processedRequest)
    val processedResponse = responseIntercept(httpResponse,context)
    val response = adapt(processedResponse)
    httpResponse.close()
    response
  }

}
