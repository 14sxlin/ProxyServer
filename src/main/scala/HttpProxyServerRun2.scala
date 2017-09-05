import java.net.SocketException
import java.util.concurrent.ArrayBlockingQueue

import connection._
import connection.control.ClientServicePool
import connection.dispatch.{ClientRequestDispatcher, RequestConsumeThread}
import constants.{ConnectionConstants, LoggerMark}
import entity.request._
import entity.request.adapt.{NoneBodyRequestAdapter, RequestAdapter, RequestWithBodyAdapter}
import filter.RequestFilterChain
import http.{ConnectionPoolingClient, RequestProxy}
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

  val connectionPoolingClient = new ConnectionPoolingClient
  val requestProxy = new RequestProxy(connectionPoolingClient)
  val requestQueue = new ArrayBlockingQueue[RequestUnit](ConnectionConstants.maxConnection)

  val clientPool = new ClientServicePool
  val requestDispatcher = new ClientRequestDispatcher(clientPool)

  val requestConsumeThread1 = new RequestConsumeThread(clientPool,requestQueue,requestProxy)
  requestConsumeThread1.setName("Request-Consume-Thread1")
  requestConsumeThread1.setDaemon(true)
  requestConsumeThread1.setPriority(10)
  requestConsumeThread1.start()
  val requestConsumeThread2 = new RequestConsumeThread(clientPool,requestQueue,requestProxy)
  requestConsumeThread2.setName("Request-Consume-Thread2")
  requestConsumeThread2.setPriority(10)
  requestConsumeThread2.start()
  val requestConsumeThread3 = new RequestConsumeThread(clientPool,requestQueue,requestProxy)
  requestConsumeThread3.setName("Request-Consume-Thread3")
  requestConsumeThread3.setPriority(10)
  requestConsumeThread3.start()




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

  val runGroup = new ThreadGroup("process-theads")

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

  // maybe this can make a conform api
  private def startNewThreadToProcess(client: ClientConnection):Unit = {
    val request = readAndParseToRequest(client)
    request match {
      case EmptyRequest => //return
        logger.warn(s"${LoggerMark.resource} empty request..close socket")
        client.closeAllResource()
      case request: TotalEncryptRequest => //ssl
        logger.warn(s"${LoggerMark.process} total encrypt request")

      case request:HeaderRecognizedRequest if request.method =="CONNECT" =>
        val uri = request.uri
        val host = uri.split(":").head.trim
        val port = uri.split(":").last.trim.toInt
        val establishInfo = HttpUtils.establishConnectInfo
        client.writeTextData(establishInfo)
//        authenticate = false
        response443AndStartCommunicate(client,host,port)
//        logger.info(s"${LoggerMark.resource} not to process, close resource")
//        client.closeAllResource()
      case request : HeaderRecognizedRequest => //post get
        val hash = HashUtils.getHash(client,request)
        processGetOrPostRequest(hash,request,client)
        Thread.`yield`()
        @tailrec
        def readRemainingRequest() : Unit = {
          val newRequest = readAndParseToRequest(client)
          if(newRequest != EmptyRequest){
            processGetOrPostRequest(hash, //todo this asInstance is dangerous
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
    client.readBinaryData() match {
      case Some(rawRequest) =>
        logger.info(s"${LoggerMark.up} raw in String: \n" + new String(rawRequest))
        RequestFactory.buildRequest(rawRequest) match {
          case r : TotalEncryptRequest => r
          case r : HeaderRecognizedRequest =>
            RequestFilterChain.handle(r)
        }
      case None =>
        //todo should close resource here
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
//      case _ =>
//        logger.error("unable to process :" + request.mkHttpStringOfFirstLineAndHeaders)
//        throw new MatchError("unable to process " + request.mkHttpStringOfFirstLineAndHeaders)
    } // great can assign object to variable
    val httpUriRequest = adapter.adapt(request)
    if(requestDispatcher.containsKey(hash)){
      logger.info(s"${LoggerMark.resource} Already have a ClientServiceUnit $hash, rest ${requestQueue.size()}")
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      logger.info(s"${LoggerMark.process} put new request")
      requestQueue.put(requestUnit)
      connectionPoolingClient.closeIdleConnection(2)
    }else{
      logger.info(s"${LoggerMark.resource} Create a new ClientServiceUnit $hash, rest ${requestQueue.size()}")
      val serviceUnit = new ClientServiceUnit(clientConnection,HttpClientContext.create())
      requestDispatcher.addNewServiceUnit(hash,serviceUnit)
      val requestUnit = requestDispatcher.buildRequestUnit(hash,httpUriRequest)
      logger.info(s"${LoggerMark.process} put new request")
      requestQueue.put(requestUnit)
      logger.info(s"${LoggerMark.resource} queue size = ${requestQueue.size()}")
      connectionPoolingClient.closeIdleConnection(2)

    }
  }


  var authenticate = true
  def response443AndStartCommunicate(client: ClientConnection, host:String, port:Int): Unit = {
    if(authenticate) {
      val communicateRun = new Runnable {
        override def run(): Unit = {
          val serverCon = new ServerConnection(host,port)
          serverCon.openConnection()
          val transfer = new DataTransfer(client,serverCon)
          transfer.communicate()
        }
      }
      val communicateThread = new Thread(communicateRun)
      communicateThread.setName("Communication-Thread")
      communicateThread.start()
//      serverCon.closeWhenNotActiveIn(5000L)
//      client.closeWhenNotActiveIn(5000L)
    }else{
      client.writeTextData(HttpUtils.unauthenicationInfo)
      client.closeAllResource()
    }
  }


  }
