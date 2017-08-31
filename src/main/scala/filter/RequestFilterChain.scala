package filter

import entity.request.Request
import filter.request.{InvalidUrlCharacterFilter, ProxyHeaderFilter, RequestContentLengthFilter}

/**
  * Created by linsixin on 2017/8/31.
  */
object RequestFilterChain {

  val requestFilters = Array(
//    ProxyHeaderFilter,
    RequestContentLengthFilter,
    InvalidUrlCharacterFilter
  )

  def handle(request:Request):Request = {
    var newRequest = request
    requestFilters.foreach{ handler =>
      newRequest = handler.handle(newRequest)
    }
    newRequest
  }
}
