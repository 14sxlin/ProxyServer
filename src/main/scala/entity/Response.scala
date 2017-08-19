package entity

import entity.request.EntityUtils.header2String

/**
  * Created by linsixin on 2017/8/12.
  */
case class Response(firstLine:String,
                    headers: Array[(String, String)],
                    body:String){

  /**
    * format to http response format
    * @return
    */
  def mkHttpString: String = {
    val str = new StringBuilder
    str.append(s"$firstLine\r\n")
      .append(headers.map(header2String).mkString("\r\n"))
      .append("\r\n"*2)
      .append(s"$body").toString()
  }
}
