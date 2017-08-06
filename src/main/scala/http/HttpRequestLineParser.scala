package http

/**
  * Created by sparr on 2017/8/6.
  */
object HttpRequestLineParser {

  /**
    * parse http request line
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
