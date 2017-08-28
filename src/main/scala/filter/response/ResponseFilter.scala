package filter.response

import entity.response.Response

/**
  * Created by linsixin on 2017/8/23.
  */
trait ResponseFilter {

  def handle(response: Response):Response

}
