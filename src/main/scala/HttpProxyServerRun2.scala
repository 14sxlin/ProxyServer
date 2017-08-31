import java.net.SocketException
import java.util.concurrent.ArrayBlockingQueue

import connection._
import connection.control.{ClientServicePool, IdleServiceGCThread}
import connection.dispatch.{RequestConsumeThread, RequestDispatcher}
import constants.LoggerMark
import entity.request._
import entity.request.adapt.{GetRequestAdapter, PostRequestAdapter, RequestAdapter}
import entity.request.wrapped.{MessRequest, RequestWrapper, WrappedRequest}
import filter.RequestFilterChain
import http.{ConnectionPoolingClient, RequestProxy}
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory
import utils.http.{HashUtils, HttpUtils}

import scala.annotation.tailrec
import scala.concurrent.forkjoin.ForkJoinPool

/**
  * Created by linsixin on 2017/8/25.
  */
object HttpProxyServerRun2 extends App {

  val logger = LoggerFactory.getLogger(getClass)
  val port = 689

  val connectionPoolingClient = new ConnectionPoolingClient
  val requestProxy = new RequestProxy(connectionPoolingClient)
  val requestQueue = new ArrayBlockingQueue[RequestUnit](ConnectionConstants.maxConnection)

  val clientPool = new ClientServicePool
  val requestDispatcher = new RequestDispatcher(clientPool)

  val requestConsumeThread1 = new RequestConsumeThread(clientPool,requestQueue,requestProxy)
  requestConsumeThread1.setName("Request-Consume-Thread1")
  requestConsumeThread1.start()
  val requestConsumeThread2 = new RequestConsumeThread(clientPool,requestQueue,requestProxy)
  requestConsumeThread2.setName("Request-Consume-Thread2")
  requestConsumeThread2.start()
  val requestConsumeThread3 = new RequestConsumeThread(clientPool,requestQueue,requestProxy)
  requestConsumeThread3.setName("Request-Consume-Thread3")
  requestConsumeThread3.start()




  val clientServiceGCThread = new IdleServiceGCThread[ClientServiceUnit](clientPool)
  clientServiceGCThread.setName("ClientService-GC")
  clientServiceGCThread.start()

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

  val executor = new ForkJoinPool(50)

  def begin(): Unit = {
    val receiver = ConnectionReceiver(port)
    val clientConnection = receiver.accept()

    clientConnection.openConnection()

    val processRun = new Runnable {
      override def run(): Unit =
        try{
          startNewThreadToProcess(clientConnection)
        }catch {
          case e: SocketException =>
            logger.warn(s"socket has closed, ${e.getMessage}")
        }
    }
    executor.execute(processRun)


//    val processThread = new Thread(processRun)
//    processThread.setName(s"Process-Thread-" +
//      s"${clientConnection.socket.getRemoteSocketAddress.toString}")
//    processThread.start()
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
        val request = RequestFilterChain.handle(
            RequestFactory.buildRequest(rawRequest)
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
      logger.info(s"${LoggerMark.resource} Already have a ClientServiceUnit $hash, rest ${requestQueue.size()}")
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      logger.info(s"${LoggerMark.process} put new request")
      requestQueue.put(requestUnit)
    }else{
      logger.info(s"${LoggerMark.resource} Create a new ClientServiceUnit $hash, rest ${requestQueue.size()}")
      val serviceUnit = new ClientServiceUnit(clientConnection,HttpClientContext.create())
      requestDispatcher.addNewServiceUnit(hash,serviceUnit)
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      logger.info(s"${LoggerMark.process} put new request")
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
