package controller

import java.net.{Socket, SocketTimeoutException}
import java.util.concurrent.ArrayBlockingQueue

import config.MyDefaultConfig
import connection.dispatch.RequestDispatcher
import connection.pipe.DataPipe
import connection.{ClientConnection, ServerConnection}
import constants.{ConfigNames, LoggerMark}
import entity.request._
import entity.request.adapt.{NoneBodyRequestAdapter, RequestAdapter, RequestWithBodyAdapter}
import filter.RequestFilterChain
import model.{ContextUnit, RequestUnit}
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory
import utils.{HashUtils, HttpUtils}

import scala.annotation.tailrec

/**
  * Created by linsixin on 2017/9/16.
  */
class RequestController(requestDispatcher:RequestDispatcher,
                        requestQueue: ArrayBlockingQueue[RequestUnit]) {

  private val logger = LoggerFactory.getLogger(getClass)
  private val readTimeout: Int = MyDefaultConfig.config.getInt(ConfigNames.readTimeout)
  def startProcess(client: ClientConnection):Unit = {
    ifTimeOutThenClose(client){
      val request = readAndParseToRequest(client)
      processRequest(request,client)
    }
  }

  private def readAndParseToRequest(client: ClientConnection):Request = {
    client.readBinaryData() match {
      case Some(rawRequest) =>
//        logger.info(s"${LoggerMark.up} raw in String: \n" +
//          StringUtils.substringBefore(new String(rawRequest),"\n")
//          //          new String(rawRequest)
//        )
        RequestFactory.buildRequest(rawRequest) match {
          case r : TotalEncryptRequest => r
          case r : HeaderRecognizedRequest =>
            RequestFilterChain.handle(r)
        }
      case None => EmptyRequest
    }
  }

  protected def processRequest(request: Request,client: ClientConnection): Unit = {
    request match {
      case EmptyRequest => //return
        logger.warn(s"${LoggerMark.resource} empty able..close socket")
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
          }
          else{
            //TODO maybe should do something
            logger.info(s"${LoggerMark.process} nothing to read ..close and remove context")
           closeConAndRemoveContext(hash,client)
          }

        }
        readRemainingRequest()

    }
  }

  private def closeConAndRemoveContext(hash:String,client: ClientConnection) :Unit = {
    requestDispatcher.removeExistContextUnit(hash)
    client.closeAllResource()
  }

  /**
    *
    * @param hash use HashUtils.getHash(client,able) to get hash value
    */
  protected def processGetOrPostRequest(hash:String,
                                        request: HeaderRecognizedRequest,
                                        clientConnection: ClientConnection):Unit = {
    putRequestWithContext(hash,requestToHttpUriRequest(request),clientConnection)
  }

  protected def requestToHttpUriRequest(request: HeaderRecognizedRequest) : HttpUriRequest= {
    var adapter: RequestAdapter = null
    request match {
      case _ : EmptyBodyRequest =>
        adapter = NoneBodyRequestAdapter
      case _ =>
        adapter = RequestWithBodyAdapter
    } // great can assign object to variable
    adapter.adapt(request)
  }

  protected def putRequestWithContext(hash:String,
                                      httpUriRequest: HttpUriRequest,
                                      clientConnection: ClientConnection):Unit = {
    if(requestDispatcher.containsKey(hash))
      putRequestUnit(hash,httpUriRequest)
    else{
      createAndPutContextUnit(hash,httpUriRequest,clientConnection)
      putRequestUnit(hash,httpUriRequest)
    }
  }

  protected def createAndPutContextUnit(hash:String,
                                        httpUriRequest: HttpUriRequest,
                                        clientConnection: ClientConnection): Unit = {
    val contextUnit = ContextUnit(clientConnection,HttpClientContext.create())
    requestDispatcher.addNewContextUnit(hash,contextUnit)
  }

  protected def putRequestUnit(hash:String, httpUriRequest: HttpUriRequest): Unit = {
    val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
    requestQueue.put(requestUnit)
    logger.info(s"${LoggerMark.resource} rest : ${requestQueue.size()}")
  }

  var authenticate = true

  private def start433Communicate(client: ClientConnection, host:String, port:Int): Unit = {
    if(authenticate) {
      client.setReadTimeout(readTimeout)
      val serverSocket = new Socket(host,port)
      val serverCon = new ServerConnection(serverSocket,s"$host:$port")
      serverCon.setReadTimeout(readTimeout)
      serverCon.openConnection()
      val transfer = new DataPipe(client,serverCon)
      transfer.startCommunicate()
      //todo when to close transfer
    }else{
      client.writeTextData(HttpUtils.unauthenicationInfo)
      client.closeAllResource()
    }
  }

  protected def ifTimeOutThenClose(client: ClientConnection)(run : => Unit): Unit ={
    try{
      run
    }catch {
      case e: SocketTimeoutException =>
        logger.error(e.getMessage + " .. close resource..")
        client.closeAllResource()
    }
  }

}
