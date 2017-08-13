package utils.http

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/12.
  */
object HttpUtils {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  /**
    * transform nameValue pair to http request header string
    * @param nameValue a name value tuple
    * @return
    */
  def header2String(nameValue:(String,String)) : String = {
    val r = s"${nameValue._1}: ${nameValue._2}"
    logger.debug(s"$nameValue  to : $r")
    r
  }

  /**
    * transform post data in raw to (name,value) array<br/>
    * to post request by HttpClient
    * @param body http request body part
    * @return
    */
  def postBody2Param(body:String) : Array[(String,String)] = {
    logger.debug(s"postBody : $body")
    val params = body.split("&")
    params.map(splitEq)
  }

  /**
    *
    * @param nameEqValue name=value
    * @return (name,value)
    */
  private def splitEq(nameEqValue:String): (String,String) ={
    if(nameEqValue==null || !nameEqValue.contains("="))
      throw new IllegalArgumentException(s"$nameEqValue is not a=b form")
    val index = nameEqValue.indexOf("=")
    (nameEqValue.substring(0,index),nameEqValue.substring(index+1))
  }

}
