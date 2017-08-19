import connection.{ClientConnection, ConnectionReceiver, DefaultConnectionPool}
import entity.request.{GetRequestAdapter, PostRequestAdapter, RequestAdapter, RequestFactory}
import org.slf4j.LoggerFactory
import task.TaskFactory

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

  private def startNewThreadToProcess(clientConnection: ClientConnection) = {
    clientConnection.openConnection()

    val requestRaw = clientConnection.readData()

    val request = RequestFactory.buildRequest(requestRaw)

    logger.info("request has been set up")
    var adapter: RequestAdapter = null
    request.firstLineInfo._1 match {
      case "GET" =>
        adapter = GetRequestAdapter
      case "POST" =>
        adapter = PostRequestAdapter
    } // great

    val wrappedRequest = adapter.adapt(request)
    logger.info("request has been wrapped")

    val task = TaskFactory.createTask(wrappedRequest)
    task.onSuccess = (response) => {
      clientConnection.writeData(response.mkHttpString)
      logger.info("success has response")
      clientConnection.closeAllResource()
    }
    logger.info("task has been set up")
    task.begin()

  }




}
