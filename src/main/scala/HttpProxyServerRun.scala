import connection.{ClientConnection, ConnectionReceiver, DefaultConnectionPool, ServerConnection}
import entity.request._
import filter.header.{ContentLengthFilter, ProxyHeaderFilter}
import org.slf4j.LoggerFactory
import task.TaskFactory
import utils.http.HttpUtils

/**
  * Created by sparr on 2017/8/1.
  */
object HttpProxyServerRun extends App{

  val logger = LoggerFactory.getLogger(getClass)

  val port = 689

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
    new Thread(new Runnable {
      override def run(): Unit =
        startNewThreadToProcess(clientConnection)
    }).start()
  }

  private def startNewThreadToProcess(client: ClientConnection) = {
    client.openConnection()
    client.closeWhenNotActiveIn(10000L)

    val requestRaw = client.readTextData()

    val request =
      ContentLengthFilter.handle(
        ProxyHeaderFilter.handle(
          RequestFactory.buildRequest(requestRaw)
        )
      )

    val wrappedRequest = RequestWrapper.wrap(request)

    logger.info("request has been set up")

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

      val task = TaskFactory.createTask(httpUriRequest)
      task.onSuccess = (response) => {
        client.writeTextData(response.mkHttpString)
//        clientConnection.closeAllResource()
      }
      task.begin()
    }else{

    }

  }

  val authenticate = true
  def responseAndAskForNewData(client: ClientConnection,host:String,port:Int): Unit = {
    if(authenticate) {
      val establishInfo = HttpUtils.establishConnectInfo
      client.writeTextData(establishInfo)
      val data = client.readBinaryData()
      val serverCon = new ServerConnection(host,port)
      serverCon.openConnection()
      logger.info(s"connect to $host:$port....")
      serverCon.writeBinaryData(data)
      logger.info(s"transfer data  to server...")
      val response = serverCon.readBinaryData()
      logger.info(s"get response:\n ${new String(response)}")
      client.writeBinaryData(response)
      logger.info(s"response to client and close resource")

      serverCon.closeWhenNotActiveIn(1000L)
      client.closeWhenNotActiveIn(1000L)
    }else{
      client.writeTextData(HttpUtils.unauthenicationInfo)
      client.closeAllResource()
    }
  }




}
