package entity.body

/**
  * Created by sparr on 2017/8/19.
  */
sealed trait BodyEntity

case class TextPlain(data: String) extends BodyEntity

/**
  * represent some body that we can't process
  *
  * @param data request body data
  */
case class EncryptData(data: String) extends BodyEntity

case class FormParams(params: Array[(String, String)]) extends BodyEntity

object FormParams {
  /**
    * transform post data in raw to (name,value) array<br/>
    * to post request by HttpClient
    *
    * @param body http request body part
    * @return
    */
  def postBody2Param(body: String): Array[(String, String)] = {
    val params = body.split("&")
    params.map(splitEq)
  }

  /**
    *
    * @param nameEqValue name=value
    * @return (name,value)
    */
  private def splitEq(nameEqValue: String): (String, String) = {
    if (nameEqValue == null || !nameEqValue.contains("="))
      throw new IllegalArgumentException(s"$nameEqValue is not a=b form")
    val index = nameEqValue.indexOf("=")
    (nameEqValue.substring(0, index), nameEqValue.substring(index + 1))
  }
}