import java.net.SocketException
import java.util.concurrent.ArrayBlockingQueue

import connection._
import connection.control.{ClientServicePool, IdleServiceGCThread}
import connection.dispatch.{RequestConsumeThread, RequestDispatcher}
import constants.LoggerMark
import entity.request._
import entity.request.adapt.{GetRequestAdapter, PostRequestAdapter, RequestAdapter}
import entity.request.wrapped.{MessRequest, RequestWrapper, WrappedRequest}
import filter.request.{ProxyHeaderFilter, RequestContentLengthFilter}
import http.{ConnectionPoolingClient, RequestProxy}
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory
import utils.http.{HashUtils, HttpUtils}

import scala.annotation.tailrec

/**
  * Created by linsixin on 2017/8/25.
  */
object HttpProxyServerRun2 extends App {

  val logger = LoggerFactory.getLogger(getClass)
  val port = 689

  val connectionPoolingClient = new ConnectionPoolingClient
  val requestProxy = new RequestProxy(connectionPoolingClient)
  val requestQueue = new ArrayBlockingQueue[RequestUnit](ConnectionConstants.maxConnection)
  val requestConsumeThread = new RequestConsumeThread(requestQueue,requestProxy)
  requestConsumeThread.setName("Request-Consume-Thread")
  requestConsumeThread.start()


  val clientPool = new ClientServicePool
  val requestDispatcher = new RequestDispatcher(clientPool)

//  val clientServiceGCThread = new IdleServiceGCThread[ClientServiceUnit](clientPool)
//  clientServiceGCThread.setName("ClientService-GC")
//  clientServiceGCThread.start()

  val task = new Runnable {
    override def run() : Unit = {
      while(true){
        begin()
      }
    }
  }
  val t = new Thread(task)
  t.setName("Socket-Accept-Thread")
  t.start()


  def begin(): Unit = {
    val receiver = ConnectionReceiver(port)
    val clientConnection = receiver.accept()

    clientConnection.openConnection()

    val processThread = new Thread(new Runnable {
      override def run(): Unit =
        try{
          startNewThreadToProcess(clientConnection)
        }catch {
          case e: SocketException =>
            logger.warn(s"socket has closed, ${e.getMessage}")
        }
    })
    processThread.setName("Process-Thread")
    processThread.start()
  }

  private def startNewThreadToProcess(client: ClientConnection):Unit = {
    val wrappedRequest = readAndParseToWrappedRequest(client)
    wrappedRequest match {
      case WrappedRequest.Empty => //return
        logger.warn("empty wrapped request")
      case _: MessRequest => //return
        logger.warn("mess wrapped request")
      case _ if wrappedRequest.method =="CONNECT" =>
        val uri = wrappedRequest.uri
        val host = uri.split(":").head.trim
        val port = uri.split(":").last.trim.toInt
        //don't process 433 now
        //process it later
//        val establishInfo = HttpUtils.establishConnectInfo
//        client.writeTextData(establishInfo)
        authenticate = false
        responseAndAskForNewData(client,host,port)
      case _ => //post get
        val hash = HashUtils.getHash(client,wrappedRequest)
        processGetOrPostRequest(hash,wrappedRequest,client)
        @tailrec
        def readRemainingRequest() : Unit = {
          val newWrappedRequest = readAndParseToWrappedRequest(client)
          if(newWrappedRequest != WrappedRequest.Empty){
            processGetOrPostRequest(hash,newWrappedRequest,client)
            readRemainingRequest()
          }
        }
        readRemainingRequest()
    }

  }

  def readAndParseToWrappedRequest(client: ClientConnection):WrappedRequest = {
    client.readTextData() match {
      case Some(rawRequest) =>
        logger.info(s"${LoggerMark.up} raw \n" + rawRequest)
        val request =
          RequestContentLengthFilter.handle(
            ProxyHeaderFilter.handle(
              RequestFactory.buildRequest(rawRequest)
            )
          )
        RequestWrapper.wrap(request)
      case None =>
        WrappedRequest.Empty
    }


  }

  private def processGetOrPostRequest(hash:String,
                                      wrappedRequest: WrappedRequest,
                                      clientConnection: ClientConnection):Unit = {
    assert(wrappedRequest != WrappedRequest.Empty)
    var adapter: RequestAdapter = null
    val method = wrappedRequest.firstLineInfo._1
    method match {
      case "GET" =>
        adapter = GetRequestAdapter
      case "POST" =>
        adapter = PostRequestAdapter
    } // great can assign object to variable
    val httpUriRequest = adapter.adapt(wrappedRequest)
    if(requestDispatcher.containsKey(hash)){
      logger.info(s"Already have a ClientServiceUnit $hash")
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      requestQueue.put(requestUnit)
    }else{
      logger.info(s"Create a new ClientServiceUnit $hash")
      val serviceUnit = new ClientServiceUnit(clientConnection,HttpClientContext.create())
      requestDispatcher.addNewServiceUnit(hash,serviceUnit)
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      requestQueue.put(requestUnit)
    }
  }


  var authenticate = true
  def responseAndAskForNewData(client: ClientConnection,host:String,port:Int): Unit = {
    if(authenticate) {
      val serverCon = new ServerConnection(host,port)
      serverCon.openConnection()
      val transfer = new DataTransfer(client,serverCon)
      transfer.communicate()
//      serverCon.closeWhenNotActiveIn(1000L)
//      client.closeWhenNotActiveIn(1000L)
    }else{
      client.writeTextData(HttpUtils.unauthenicationInfo)
      client.closeAllResource()
    }
  }


  }
