import java.net.SocketException

import connection._
import connection.control.{DefaultConnectionPool, IdleConnectionThread}
import constants.LoggerMark
import entity.request._
import entity.request.adapt.{GetRequestAdapter, PostRequestAdapter, RequestAdapter}
import entity.request.dispatch.{RequestDispatcher, RequestSessionConsumeThread}
import entity.request.wrapped.{MessRequest, RequestWrapper, WrappedRequest}
import entity.response.Response
import filter.request.{ProxyHeaderFilter, RequestContentLengthFilter}
import filter.response.{ChuckFilter, ResponseContentLengthFilter}
import http.{ConnectionPoolingClient, RequestProxy}
import org.slf4j.LoggerFactory
import utils.http.{HashUtils, HttpUtils}

import scala.annotation.tailrec

/**
  * Created by linsixin on 2017/8/25.
  */
object HttpProxyServerRun2 extends App {

  val logger = LoggerFactory.getLogger(getClass)
  val port = 689
  val proxy = new RequestProxy(new ConnectionPoolingClient)

  val clientConPool = new DefaultConnectionPool
  val idleConnectionGCThread = new IdleConnectionThread(clientConPool)
  idleConnectionGCThread.setDaemon(true)
  idleConnectionGCThread.setName("ConnectionGC-Thread")
  idleConnectionGCThread.start()

  val requestConsumerPool = new DefaultConnectionPool
  val idleConsumerGCThread = new IdleConnectionThread(requestConsumerPool)
  idleConsumerGCThread.setDaemon(true)
  idleConsumerGCThread.setName("ConsumerGC-Thread")
  idleConsumerGCThread.start()

  val task = new Runnable {
    override def run() : Unit = {
      while(true){
        begin()
      }
    }
  }
  val t = new Thread(task)
  t.start()


  def begin(): Unit = {
    val receiver = ConnectionReceiver(port,clientConPool)
    val clientConnection = receiver.accept()

    clientConnection.openConnection()
    new Thread(new Runnable {
      override def run(): Unit =
        try{
          startNewThreadToProcess(clientConnection)
        }catch {
          case e: SocketException =>
            logger.warn(s"socket has closed, ${e.getMessage}")
        }
    }).start()
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
        val establishInfo = HttpUtils.establishConnectInfo
        client.writeTextData(establishInfo)
        responseAndAskForNewData(client,host,port)
      case _ => //post get
        val hash = HashUtils.getHash(client,wrappedRequest)

        val onSuccess = (response: Response) => {
          val data = ResponseContentLengthFilter.handle(
            ChuckFilter.handle(response))
            .mkHttpBinary()
          logger.info(s"${LoggerMark.down} " +
            s"${response.firstLine}  " +
            s"${data.length} to client")
          client.writeBinaryData(data)
        }
        processGetOrPostRequest(hash,wrappedRequest,onSuccess)

        @tailrec
        def readRemainingRequest() : Unit = {
          val newWrappedRequest = readAndParseToWrappedRequest(client)
          if(newWrappedRequest != WrappedRequest.Empty){
            processGetOrPostRequest(hash,newWrappedRequest,onSuccess)
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
                                       onSuccess: Response => Unit ):Unit = {
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
    val alreadyHasSession =
          RequestDispatcher.dispatch(hash,httpUriRequest)
    if(!alreadyHasSession){ //if it's a new session should start new thread
      logger.info(s"${LoggerMark.resource} start new queue consumer: $hash")
      RequestDispatcher.getRequestSession(hash) match{
        case Some(requestSession) =>
          val consumeThread = new RequestSessionConsumeThread(
            requestSession,
            proxy,
            onSuccess
          )
          requestConsumerPool.put(hash,consumeThread)
          consumeThread.setName("Queue-consumer")
          consumeThread.start()
        case None =>
          throw new Exception("no request session !")
      }
    }else{
      logger.info(s"${LoggerMark.resource} add new request to same context")
    }

  }


  val authenticate = true
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
