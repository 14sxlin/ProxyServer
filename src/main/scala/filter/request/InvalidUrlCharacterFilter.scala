package filter.request
import java.net.URLEncoder

import entity.request.{ByteBodyRequest, EmptyBodyRequest, HeaderRecognizedRequest, TextRequest}

/**
  * Created by linsixin on 2017/8/31.
  */
object InvalidUrlCharacterFilter extends RequestFilter{

  private val invalidChars = Array("|")

  override protected def format(request: HeaderRecognizedRequest):HeaderRecognizedRequest = {
    val uri = request.uri
    var newUri = ""
    def replaceInvalidChar() = {
      var tempUri = uri
      invalidChars.foreach{ invalidChar =>
        if(tempUri.contains(invalidChar))
          tempUri = tempUri.replace(invalidChar,
            URLEncoder.encode(invalidChar,"utf8"))
      }
      assert(tempUri != uri,"should replace invalid chars")
      tempUri
    }

    if(hasInvalidChar(uri)) {
      newUri = replaceInvalidChar()
      val parts = request.firstLine.split(" ")
      assert(parts.length == 3)
      parts(1) = newUri
      request.updateFirstLine(parts.mkString(" "))
    }else request
  }

  private def hasInvalidChar(uri:String) : Boolean = {
    for(invalidChar <- invalidChars){
      if(uri.contains(invalidChar))
        return true
    }
    false
  }
}
