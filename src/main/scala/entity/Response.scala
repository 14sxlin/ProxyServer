package entity

import utils.http.HttpUtils

/**
  * Created by linsixin on 2017/8/12.
  */
case class Response(firstLine:String,
                    var headers:Array[(String,String)],
                    body:String){

  /**
    * format to http response format
    * @return
    */
  def mkString: String = {
    val str = new StringBuilder
    str.append(s"$firstLine\r\n")
      .append(headers.map(HttpUtils.header2String).mkString("\r\n"))
      .append("\r\n"*2)
      .append(s"$body").toString()
  }
}
