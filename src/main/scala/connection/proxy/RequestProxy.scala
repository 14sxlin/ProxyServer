//package connection.proxy
//
//import connection.ClientConnection
//import constants.HttpRequestMethod
//import entity.request.{Request, RequestFactory}
//import entity.{RequestFactory, Response}
//import org.slf4j.{Logger, LoggerFactory}
//import task.TaskFactory
//
///**
//  * Created by sparr on 2017/8/19.
//  */
//class RequestProxy(socketConnection: ClientConnection) {
//
//  val logger : Logger = LoggerFactory.getLogger(getClass)
//
//  /**
//    *
//    * @param requestRawData request data
//    * @return false when need more info , true when info enough
//    */
//  def process(requestRawData:String): Boolean ={
//    var request = RequestFactory.buildRequest(requestRawData,
//      DefaultHeaderChain.firstOfRequestHeaderHandlerChain)
//
//  }
//
//
//  def buildAndBeginRequestTask(request:Request) : Unit = {
//    val task = TaskFactory.createTask(request)
//    task.onSuccess =(response) =>{
//      response2Client(response)
//      closeConnection()
//    }
//    task.onFail = (e) =>{
//      logger.error("",e)
//      closeConnection()
//    }
//    task.begin()
//  }
//
//
//  private def responseClientWith200Establish(request:Request) : Boolean = {
//    val success200 = "HTTP/1.1 200 Connection established\r\nHeader: nothing\r\n\r\n"
//    writer.append(success200)
//    writer.flush()
//    dontCloseConnection2Client = true
//    true
//  }
//
//  private def response2Client(response: Response) = {
//    logger.info(s"response length : ${response.body.length}")
//    val handler = DefaultHeaderChain.firstOfResponseHeaderHandlerChain
//    val newHeader = handler.handle(response.headers)
//    response.headers = newHeader
//    writer.append(response.mkHttpString)
//    writer.flush()
//    if(dontCloseConnection2Client){}
//    else
//      closeConnection()
//  }
//
//  private def closeConnection(): Unit ={
//    writer.close()
//    in.close()
//    socket.close()
//  }
//
//
//
//
//}
