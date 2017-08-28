import java.net.SocketException

import connection._
import entity.request._
import entity.request.dispatch.{RequestDispatcher, RequestSessionConsumeThread}
import entity.response.Response
import filter.request.{ContentLengthFilter, ProxyHeaderFilter}
import filter.response.ChuckFilter
import http.{ConnectionPoolingClient, RequestProxy}
import org.slf4j.LoggerFactory
import utils.http.{HashUtils, HttpUtils}

/**
  * Created by sparr on 2017/8/25.
  */
object HttpProxyServerRun2 extends App {

  val logger = LoggerFactory.getLogger(getClass)
  val port = 689
  val proxy = new RequestProxy(new ConnectionPoolingClient)

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
    val receiver = ConnectionReceiver(port, new DefaultConnectionPool)
    val clientConnection = receiver.accept()
    logger.info(s"receive connection...$clientConnection")
    clientConnection.openConnection()
    clientConnection.closeWhenNotActiveIn(10000L)
    new Thread(new Runnable {
      override def run(): Unit =
        try{
          while(true){
            startNewThreadToProcess(clientConnection)
          }
        }catch {
          case e: SocketException =>
            logger.warn("socket has closed",e)
        }
    }).start()
  }

  private def startNewThreadToProcess(client: ClientConnection) = {
    val requestRaw = client.readTextData()
    val request =
      ContentLengthFilter.handle(
        ProxyHeaderFilter.handle(
          RequestFactory.buildRequest(requestRaw)
        )
      )

    val wrappedRequest = RequestWrapper.wrap(request)

    var isConnectRequest = false
    var adapter: RequestAdapter = null
    wrappedRequest.firstLineInfo._1 match {
      case "GET" =>
        adapter = GetRequestAdapter
      case "POST" =>
        adapter = PostRequestAdapter
      case "CONNECT" =>
        val (_,uri,_) = wrappedRequest.firstLineInfo
        val host = uri.split(":").head
        val port = uri.split(":").last.toInt
        responseAndAskForNewData(client,host,port)
        isConnectRequest = true
    } // great can assign object to variable

    if(!isConnectRequest){
      val httpUriRequest = adapter.adapt(wrappedRequest)

      val hash = HashUtils.getHash(client,wrappedRequest)
      val requestSession = RequestDispatcher.dispatch(
        hash,
        httpUriRequest)

      val onSuccess = (response:Response) => {
        client.writeTextData(
          ChuckFilter.handler(response).mkHttpString
        )
      }

      new RequestSessionConsumeThread(
        requestSession,
        proxy,
        onSuccess
      )
    }else{

    }

  }

  val authenticate = true
  def responseAndAskForNewData(client: ClientConnection,host:String,port:Int): Unit = {
    if(authenticate) {
      val establishInfo = HttpUtils.establishConnectInfo
      client.writeTextData(establishInfo)
      val serverCon = new ServerConnection(host,port)
      serverCon.openConnection()
      val transfer = new DataTransfer(client,serverCon)
      transfer.communicate()
      //      logger.info(s"connect to $host:$port....")
      //      serverCon.writeBinaryData(data)
      //      logger.info(s"transfer data  to server...")
      //      val response = serverCon.readBinaryData()
      //      logger.info(s"get response:\n ${new String(response)}\n")
      //      client.writeBinaryData(response)

      serverCon.closeWhenNotActiveIn(1000L)
      client.closeWhenNotActiveIn(1000L)
    }else{
      client.writeTextData(HttpUtils.unauthenicationInfo)
      client.closeAllResource()
    }
  }


}