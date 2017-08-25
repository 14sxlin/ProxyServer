package filter.request

import entity.request.Request

/**
  * Created by sparr on 2017/8/11.
  */
trait HeaderFilter {

  def handle(request: Request): Request

}