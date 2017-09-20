import config.MyDefaultConfig.config
import connection.ConnectionReceiver
import constants.ConfigNames
import controller.FlowRequestController
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/9/19.
  */
object FlowProxyServerRun extends App{

  val logger = LoggerFactory.getLogger(getClass)

  val port = config.getInt(ConfigNames.port)
  val readTimeout: Int = config.getInt(ConfigNames.readTimeout)

  def begin(): Unit = this.synchronized{
    val receiver = ConnectionReceiver(port)
    val client = receiver.accept()
    val controller = new FlowRequestController(client)
    val processRun = new Runnable {
      override def run(): Unit = controller.process()

    }
    val processThread = new Thread(processRun)
    processThread.setName(s"Process-Thread-" +
      s"${client.socket.getRemoteSocketAddress.toString}")
    processThread.start()
  }
  val beginThread = new Thread(new Runnable {
    override def run(): Unit = while(true){
      begin()
    }
  })
  beginThread.start()
  beginThread.join()


}
