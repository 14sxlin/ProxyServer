package entity.request

import entity.response.Response

/**
  * Created by linsixin on 2017/9/15.
  * This class represents requests which
  * response is cache and needn't to send
  * request to server
  */
case class ResponseCachedRequest(absoluteUri:String,response: Response) extends HeaderRecognizedRequest("",Array.empty[(String,String)]){

  private def throwException =
    throw new Exception(s"should not call function in ResponseCachedRequest")

  override def updateFirstLine(newFirstLine: String): Nothing = throwException

  override def updateHeaders(newHeaders: Array[(String, String)]): Nothing = throwException

  override def mkHttpBinary(): Nothing = throwException
}


