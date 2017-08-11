package handler.header

import scala.beans.BeanProperty

/**
  * Created by sparr on 2017/8/11.
  */
abstract class HeaderHandler {

  @BeanProperty
  var nextHandler: HeaderHandler = _

  def handler(headers: Array[(String, String)]):Array[(String,String)]
}