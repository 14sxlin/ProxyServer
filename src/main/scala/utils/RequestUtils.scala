package utils

/**
  * Created by linsixin on 2017/8/19.
  */
object RequestUtils {

  /**
    * transform nameValue pair to http request header string
    *
    * @param nameValue a name value tuple
    * @return
    */
  def header2String(nameValue: (String, String)): String = {
    s"${nameValue._1}: ${nameValue._2}"
  }
}
