package http

import java.io._
import java.net.{ServerSocket, Socket, UnknownHostException}

import exception.NotHeaderException
import mock.client.HttpClientMock
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by sparr on 2017/7/30.
  */
class HttpProxyServer {

  val logger = LoggerFactory.getLogger(getClass)
  val port689 = 689
  val NOT_FOUND = -1

  private var targetHost : String = _

  private var reader : BufferedReader = _
  private var writer : PrintWriter = _
  private var client : Socket = _

  def accept() : Unit = {

    logger.info("http server is waiting at {} ",port689)

    val socketServer = new ServerSocket(port689)
    client = socketServer.accept()

    logger.info("{} has connected",client.getInetAddress)

    val thread = beginProcessThread(client)
    thread.join()
//    writer.close()
//    client.close()

    Thread.sleep(5000)
    logger.info("http server has process a task {}",client.toString)

  }

  private def beginProcessThread(client:Socket):Thread = {
    if(client == null)
    {
      logger.info("no client has connected ")
      throw new IllegalArgumentException(s"client should not be null")
    }
    reader = new BufferedReader(new InputStreamReader(client.getInputStream))
    writer = new PrintWriter(client.getOutputStream)

    val task = new Runnable {
      override def run() : Unit = processClientRequest()
    }
    val processThread= new Thread(task)
    processThread.start()
    processThread
  }

  private def processClientRequest() : Unit = {
    val requestLine = reader.readLine()
    val (method,uri,version) =
      HttpRequestLineParser.parse(requestLine)

    logger.info("<method : {} >; <uri : {} > ; <version: {}>",method,uri,version)

    val headers = parseHeaders()
    doRequestAndResponse(headers,uri)

  }

  private def parseHeaders() : Array[(String,String)] = {
    logger.info("begin to parse headers")
    var line = reader.readLine()
    val nameValues = ArrayBuffer[(String,String)]()
    while(line != ""){
      logger.info(line)
      nameValues += parseHeader(line)
      line = reader.readLine()
    }
    logger.info("headers parse finish")
    nameValues.toArray
  }

  private def parseHeader(line : String): (String,String) ={
    val index = line.indexOf(":")
    if(index == NOT_FOUND){
      throw new NotHeaderException(line)
    }
    val name = line.substring(0,index).trim
    val value = line.substring(index+1).trim
    (name,value)
  }

  private def doRequestAndResponse(headers : Array[(String,String)],uri:String) = {
    getValueByHeader(headers,HttpRequestHeaders.HOST) match {
      case Some(host) =>
        doGetAndResponseClient(host,uri)
      case None =>
        throw new UnknownHostException("no host found")
    }
  }

  private def doGetAndResponseClient(host:String,uri:String)  = {
    val requestFuture = Future{
      logger.info(s"request : doGet: $uri")
      new HttpClientMock().doGet(uri)
    }

    requestFuture.failed.foreach((exception) =>{
      logger.error("some error occur",exception)
    })
    requestFuture.foreach{
      case content:String =>
        logger.info("get content-length : {}",content.length)
        sendResponseToClient(content)
      case _ =>
        throw new IOException(s"cannot get uri: host :  $host" +
          s"\r\n uri : $uri")
    }
  }

  private def getValueByHeader(allHeaders: Array[(String,String)],
                             targetHeader: String) : Option[String] = {
    allHeaders.find((keyValuePair) => {
        if (keyValuePair._1 == targetHeader) true else false
    }) match {
      case Some(keyValue) =>
        Some(keyValue._2)
      case None =>
        None
    }
  }

  private def sendResponseToClient(response:String) = {
    writer.append("HTTP/1.1 200 OK\r\n")
    writer.append("Content-Type: text/html; charset=UTF-8\r\n")
    writer.append("Server: sparrowxin-Proxy\r\n")
    writer.append(s"Content-Length: ${response.length}\r\n\r\n")
    writer.append(response)
    writer.flush()
    logger.info("content has been send to client")
    writer.close()
    reader.close()
    client.close()
    logger.info("client socket close")
  }


}

