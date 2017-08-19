package exception

/**
  * Created by sparr on 2017/8/1.
  */
class NotHeaderException(errorLine:String) extends Exception{
  override def getMessage: String = {
    s"<$errorLine>" +
      " is not a valid header which form is Key: Value \r\n" +
      super.getMessage
  }

}
