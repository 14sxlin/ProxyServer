import java.net.{Socket, SocketException, SocketTimeoutException}
import java.util.concurrent.ArrayBlockingQueue

import connection._
import connection.dispatch.{ClientRequestDispatcher, RequestConsumeThread}
import connection.pool.{ClientServiceUnitPool, ServerConnectionPool}
import constants.{ConnectionConstants, LoggerMark, Timeout}
import entity.request._
import entity.request.adapt.{NoneBodyRequestAdapter, RequestAdapter, RequestWithBodyAdapter}
import filter.RequestFilterChain
import http.{ConnectionPoolingClient, RequestProxy}
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory
import utils.{HashUtils, HttpUtils}

import scala.annotation.tailrec

/**
  * Created by linsixin on 2017/8/25.
  */
object HttpProxyServerRun2 extends App {

  val logger = LoggerFactory.getLogger(getClass)
  val port = 689


  val requestQueue = new ArrayBlockingQueue[RequestUnit](ConnectionConstants.maxConnection)

  val clientPool = new ClientServiceUnitPool
  val requestDispatcher = new ClientRequestDispatcher(clientPool)

  val serverConPool = new ServerConnectionPool[ServerConnection]()

  for( i <- 1 to 3){
    val connectionPoolingClient = new ConnectionPoolingClient
    val requestProxy = new RequestProxy(connectionPoolingClient)
    val requestConsumeThread = new RequestConsumeThread(clientPool,requestQueue,requestProxy)
    requestConsumeThread.setName(s"Request-Consume-Thread$i")
    requestConsumeThread.setDaemon(true)
    requestConsumeThread.setPriority(10)
    requestConsumeThread.start()
  }


//  val clientServiceGCThread = new IdleServiceGCThread[ClientServiceUnit](clientPool)
//  clientServiceGCThread.setName("ClientService-GC")
//  clientServiceGCThread.start()

  val beginTask = new Runnable {
    override def run() : Unit = {
      while(true){
        begin()
      }
    }
  }
  val t = new Thread(beginTask)
  t.setName("Socket-Accept-Thread")
  t.start()

  val runGroup = new ThreadGroup("Process-Theads")

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


    val processThread = new Thread(runGroup,processRun)
    processThread.setName(s"Process-Thread-" +
      s"${clientConnection.socket.getRemoteSocketAddress.toString}")
    processThread.setPriority(3)
    processThread.start()
    logger.info(s"${LoggerMark.resource} process-active-count: ${runGroup.activeCount()}")
  }

  // todo maybe this can make a conform api
  private def startNewThreadToProcess(client: ClientConnection):Unit = {
    val request = readAndParseToRequest(client)
    request match {
      case EmptyRequest => //return
        logger.warn(s"${LoggerMark.resource} empty request..close socket")
        client.closeAllResource()
      case _: TotalEncryptRequest => //ssl
        throw new Exception("unable to process")
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
        @tailrec
        def readRemainingRequest() : Unit = {
          val newRequest = readAndParseToRequest(client)
          if(newRequest != EmptyRequest){
            processGetOrPostRequest(hash, //TODO this asInstance is dangerous
              newRequest.asInstanceOf[HeaderRecognizedRequest],client)
            readRemainingRequest()
          }else{
            //TODO maybe should do something
            logger.info(s"${LoggerMark.process} nothing to read ..close resource ")
            client.closeAllResource()
          }
        }
        readRemainingRequest()
    }

  }

  def readAndParseToRequest(client: ClientConnection):Request = {
    //todo catch readTimeout exception
    try{
      client.readBinaryData() match {
        case Some(rawRequest) =>
          logger.info(s"${LoggerMark.up} raw in String: \n" +
            StringUtils.substringBefore(new String(rawRequest),"\n"))
          RequestFactory.buildRequest(rawRequest) match {
            case r : TotalEncryptRequest => r
            case r : HeaderRecognizedRequest =>
              RequestFilterChain.handle(r)
          }
        case None => EmptyRequest
      }
    }catch {
      case _:SocketTimeoutException =>
        logger.info(s"${LoggerMark.process} read timeout..ready to close resource ")
        EmptyRequest
    }


  }

  private def processGetOrPostRequest(hash:String,
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
      logger.info(s"${LoggerMark.resource} Already have a ClientServiceUnit $hash, rest ${requestQueue.size()}")
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      requestQueue.put(requestUnit)
//      connectionPoolingClient.closeIdleConnection(ConnectionConstants.idleThreshold.toInt)
    }else{
      logger.info(s"${LoggerMark.resource} Create a new ClientServiceUnit $hash, rest ${requestQueue.size()}")
      val serviceUnit = new ClientServiceUnit(clientConnection,HttpClientContext.create())
      requestDispatcher.addNewServiceUnit(hash,serviceUnit)
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      requestQueue.put(requestUnit)
//      connectionPoolingClient.closeIdleConnection(ConnectionConstants.idleThreshold.toInt)
    }
  }


  var authenticate = true
  def start433Communicate(client: ClientConnection, host:String, port:Int): Unit = {
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
