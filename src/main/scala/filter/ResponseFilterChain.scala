package filter

import entity.response.Response
import filter.response.{ChunkFilter, ConnectionCloseFilter, ResponseContentLengthFilter}

/**
  * Created by linsixin on 2017/8/31.
  */
object ResponseFilterChain {

  val responseFilters = Array(
    ChunkFilter,
    ResponseContentLengthFilter,
    ConnectionCloseFilter
  )

  def handle(response:Response):Response = {
    var newResponse = response
    responseFilters.foreach{ filter =>
      newResponse = filter.handle(newResponse)
    }
    newResponse
  }
}
