package filter.response

import entity.response.Response

/**
  * Created by sparr on 2017/8/23.
  */
trait ResponseFilter {

  def handle(response: Response):Response

}
