package filter

import entity.response.Response
import filter.response.{ChunkFilter, ResponseContentLengthFilter}

/**
  * Created by linsixin on 2017/8/31.
  */
object ResponseFilterChain {

  val responseFilters = Array(
    ChunkFilter,
    ResponseContentLengthFilter
  )

  def handle(response:Response):Response = {
    var newResponse = response
    responseFilters.foreach{ filter =>
      newResponse = filter.handle(response)
    }
    newResponse
  }
}
