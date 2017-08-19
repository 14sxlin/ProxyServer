//package http
//
//import java.io._
//import java.net.{ServerSocket, Socket, UnknownHostException}
//
//import constants.HttpRequestHeaders
//import exception.NotHeaderException
//import org.slf4j.{Logger, LoggerFactory}
//
//import scala.collection.mutable.ArrayBuffer
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//
//
///**
//  * Created by sparr on 2017/7/30.
//  */
//class HttpProxyServer {
//
//  val logger : Logger = LoggerFactory.getLogger(getClass)
//  val port689 = 689
//  val NOT_FOUND : Int = -1
//
////  private var targetHost : String = _
//
//  private var reader : BufferedReader = _
//  private var writer : PrintWriter = _
//  private var client : Socket = _
//  private var socketServer : ServerSocket = _
//
//  def accept() : Unit = {
//
//    logger.info("http server is waiting at {} ",port689)
//
//    socketServer = new ServerSocket(port689)
//    client = socketServer.accept()
//    socketServer.close()
//
//    logger.info("{} has connected",client.getInetAddress)
//
//    beginProcessThread(client)
////    writer.close()
////    client.close()
//
//    logger.info("http server has process a task {}",client.toString)
//
//  }
//
//  private def beginProcessThread(client:Socket):Unit = {
//    reader = new BufferedReader(new InputStreamReader(client.getInputStream))
//    writer = new PrintWriter(client.getOutputStream)
//
//    val task = new Runnable {
//      override def run() : Unit = processClientRequest()
//    }
//    new Thread(task).start()
//  }
//
//  private def processClientRequest() : Unit = {
//    val requestLine = reader.readLine()
//    val (method,uri,version) =
//      HttpRequestLineParser.parse(requestLine)
//
//    logger.info("<method : {} >; <uri : {} > ; <version: {}>",method,uri,version)
//
//    val headers = parseHeaders()
//    doRequestAndResponse(headers,uri)
//
//  }
//
//
//
//
//
//  private def doRequestAndResponse(headers : Array[(String,String)],uri:String) = {
//    getValueByHeader(headers,HttpRequestHeaders.HOST) match {
//      case Some(host) =>
//        doGetAndResponseClient(host,uri)
//      case None =>
//        throw new UnknownHostException("no host found")
//    }
//  }
//
//  private def doGetAndResponseClient(host:String,uri:String)  = {
//    val requestFuture = Future{
//      logger.info(s"request : doGetByHttpClient: $uri")
//      HttpUtils.doGetByHttpClient(uri)
//    }
//
//    requestFuture.failed.foreach((exception) =>{
//      logger.error("some error occur",exception)
//    })
//    requestFuture.foreach{
//      case (statusLine,headers,responseBody) =>
//        sendResponseToClient(statusLine,headers,responseBody)
//      case _ =>
//        throw new IOException(s"cannot get uri: host :  $host" +
//          s"\r\n uri : $uri")
//    }
//  }
//
//  private def getValueByHeader(allHeaders: Array[(String,String)],
//                             targetHeader: String) : Option[String] = {
//    allHeaders.find((keyValuePair) => {
//        if (keyValuePair._1 == targetHeader) true else false
//    }) match {
//      case Some(keyValue) =>
//        Some(keyValue._2)
//      case None =>
//        None
//    }
//  }
//
//  private def sendResponseToClient(responseLine:String,
//                                   headers:Array[(String,String)],
//                                   body:String) = {
//    fillResponseLine(responseLine)
//    fillHeaders(headers)
//    fillResponse(body)
//    closeResource()
//    logger.info("content has been send to client")
//  }
//
//  private def fillResponseLine(responseLine:String) =
//    writer.append(responseLine)
//
//  private def fillHeaders(headers:Array[(String,String)]) = {
//    for( (name,value) <- headers)
//      writer.append(s"$name: $value \r\n")
//    writer.append("\r\n")
//  }
//
//  private def fillResponse(body:String) =
//    writer.append(body)
//
//  private def closeResource(): Unit ={
//    writer.flush()
//    writer.close()
//    reader.close()
//    client.close()
//    logger.info("client socket close")
//  }
//
//
//}
//
