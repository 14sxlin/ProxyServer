package entity

/**
  * Created by linsixin on 2017/8/12.
  */
case class Request(firstLine:String,
                   headers:Array[(String,String)],
                   body:String){
  val (method,uri,version) = parse(firstLine)

  /**
    * format to http request format
    * @return
    */
  def mkString: String = {
    val str = new StringBuilder
    str.append(s"$firstLine\r\n")
      .append(headers.map(header2String).mkString("\r\n"))
      .append("\r\n"*2)
      .append(s"$body").toString()
  }

  private def header2String(nameValue:(String,String)) = {
    s"${nameValue._1}: ${nameValue._2}"
  }

  /**
    * parse http request request first line
    * @param requestLine http request line
    * @return (request-method,request-uri,http-version)
    */
  def parse(requestLine:String):(String,String,String) = {
    if(isUnknownRequestLine(requestLine))
      throw new IllegalArgumentException(s"$requestLine is not a http request line")
    val partsOfLine = requestLine.trim.split(" ")
    val method = partsOfLine(0)
    val uri = partsOfLine(1)
    val version = partsOfLine(2)
    (method,uri,version)
  }

  private def isUnknownRequestLine(requestLine:String) = {
    val firstBlankIndex = requestLine.indexOf(" ")
    val lastBlankIndex = requestLine.lastIndexOf(" ")
    firstBlankIndex == -1 ||
      lastBlankIndex == -1 ||
      requestLine.split(" ").length!=3
  }

}

