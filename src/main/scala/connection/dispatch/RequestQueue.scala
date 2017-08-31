package connection.dispatch

import java.util.concurrent.ArrayBlockingQueue

import org.apache.http.client.methods.HttpUriRequest

/**
  * Created by linsixin on 2017/8/25.
  */
class RequestQueue{

  private val queue = new ArrayBlockingQueue[HttpUriRequest](10)

  def put(request:HttpUriRequest): Unit ={
    queue.put(request)
  }

  def take() : HttpUriRequest = {
    queue.take()
  }

}
