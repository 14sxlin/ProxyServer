package entity.request

/**
  * Created by sparr on 2017/8/19.
  */
object EntityUtils {

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
