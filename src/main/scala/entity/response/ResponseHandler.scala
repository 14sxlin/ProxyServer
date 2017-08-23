package entity.response

/**
  * Created by sparr on 2017/8/23.
  */
trait ResponseHandler {

  def handler(response: Response):Response

}
