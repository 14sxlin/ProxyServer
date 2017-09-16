package controller

import java.net.Socket
import java.util.concurrent.ArrayBlockingQueue

import connection.dispatch.ClientRequestDispatcher
import connection.{ClientConnection, ClientServiceUnit, DataTransfer, ServerConnection}
import constants.{LoggerMark, Timeout}
import entity.request._
import entity.request.adapt.{NoneBodyRequestAdapter, RequestAdapter, RequestWithBodyAdapter}
import filter.RequestFilterChain
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory
import utils.{HashUtils, HttpUtils}

import scala.annotation.tailrec

/**
  * Created by linsixin on 2017/9/16.
  */
class RequestController(requestDispatcher:ClientRequestDispatcher,
                        requestQueue: ArrayBlockingQueue[RequestUnit]) {

  private val logger = LoggerFactory.getLogger(getClass)

  def startProcess(client: ClientConnection):Unit = {
    val request = readAndParseToRequest(client)
    processRequest(request,client)
  }

  protected def processRequest(request: Request,client: ClientConnection): Unit = {
    request match {
      case EmptyRequest => //return
        logger.warn(s"${LoggerMark.resource} empty request..close socket")
        client.closeAllResource()
      case e: TotalEncryptRequest => //ssl
        throw new Exception(s"unable to process, ${new String(e.bytes)}")
      case request:HeaderRecognizedRequest if request.method =="CONNECT" =>
        val uri = request.uri
        val host = uri.split(":").head.trim
        val port = uri.split(":").last.trim.toInt
        val establishInfo = HttpUtils.establishConnectInfo
        client.writeBinaryData(establishInfo.getBytes())
        start433Communicate(client,host,port)
      case request : HeaderRecognizedRequest => //post get
        val hash = HashUtils.getHash(client,request)
        processGetOrPostRequest(hash,request,client)
        @tailrec //todo catch read time out
        def readRemainingRequest() : Unit = {
          val newRequest = readAndParseToRequest(client)
          if(newRequest != EmptyRequest){
            processGetOrPostRequest(hash, //TODO this asInstance is dangerous
              newRequest.asInstanceOf[HeaderRecognizedRequest],client)
            readRemainingRequest()
          }else{
            //TODO maybe should do something
            logger.info(s"${LoggerMark.process} nothing to read ..sleep ")
            Thread.sleep(1000)
            //            client.closeAllResource()
          }
        }
        readRemainingRequest()
    }
  }

  private def readAndParseToRequest(client: ClientConnection):Request = {
    client.readBinaryData() match {
      case Some(rawRequest) =>
        logger.info(s"${LoggerMark.up} raw in String: \n" +
                    StringUtils.substringBefore(new String(rawRequest),"\n")
//          new String(rawRequest)
        )
        RequestFactory.buildRequest(rawRequest) match {
          case r : TotalEncryptRequest => r
          case r : HeaderRecognizedRequest =>
            RequestFilterChain.handle(r)
        }
      case None => EmptyRequest
    }
  }

  protected def processGetOrPostRequest(hash:String,
                                      request: HeaderRecognizedRequest,
                                      clientConnection: ClientConnection):Unit = {
    var adapter: RequestAdapter = null
    request match {
      case _ : EmptyBodyRequest =>
        adapter = NoneBodyRequestAdapter
      case _ =>
        adapter = RequestWithBodyAdapter
    } // great can assign object to variable

    val httpUriRequest = adapter.adapt(request)
    if(requestDispatcher.containsKey(hash)){
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      requestQueue.put(requestUnit)
      //      connectionPoolingClient.closeIdleConnection(ConnectionConstants.idleThreshold.toInt)
    }else{
      val serviceUnit = new ClientServiceUnit(clientConnection,HttpClientContext.create())
      requestDispatcher.addNewServiceUnit(hash,serviceUnit)
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      requestQueue.put(requestUnit)
      //      connectionPoolingClient.closeIdleConnection(ConnectionConstants.idleThreshold.toInt)
    }
  }


  var authenticate = true
  protected def start433Communicate(client: ClientConnection, host:String, port:Int): Unit = {
    if(authenticate) {
      client.setReadTimeout(Timeout._443ReadTimeout)
      val serverSocket = new Socket(host,port)
      val serverCon = new ServerConnection(serverSocket,s"$host:$port")
      client.setReadTimeout(Timeout._443ReadTimeout)
      serverCon.openConnection()
      val transfer = new DataTransfer(client,serverCon)
      transfer.startCommunicate()
      //todo when to close transfer
    }else{
      client.writeTextData(HttpUtils.unauthenicationInfo)
      client.closeAllResource()
    }
  }

}
