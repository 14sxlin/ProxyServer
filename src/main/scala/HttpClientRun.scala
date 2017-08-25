import java.net.SocketException

import mock.client.HttpClientMock
import org.slf4j.LoggerFactory

/**
  * Created by sparr on 2017/8/5.
  */
object HttpClientRun extends App{
  val logger = LoggerFactory.getLogger(getClass)

//  val getUri = "http://localhost:8080/LoginDemo/"
  val getPort = 9000
  val proxyPort = 689
  val proxy = "localhost"
  val targetURI = "/"
  val targetPort = 8080
  val targetHost = "localhost"
  val httpClient = new HttpClientMock
  val postUri = "/LoginDemo/login.do"

  def get(url:String) : Thread = {
    val doGet = new Runnable {
      override def run() : Unit = {
        new HttpClientMock().doGet(url)
      }
    }
    val doGetThread = new Thread(doGet)
    doGetThread.start()
    doGetThread
  }

  def doGetByProxy():Thread = {
    @volatile var success = false
    val task = new Runnable {
      override def run() : Unit = {
        while(!success){
          try{
            val result = httpClient.doGetByProxyWithHttp(targetHost, 8080, proxy, proxyPort, targetURI)
            logger.info(s"receive data = : $result")
            success = true
          }catch {
            case e:SocketException =>
              logger.error("socket error")
              logger.info("retry 10 seconds later")
              Thread.sleep(10000)
          }
        }
      }
    }
    new Thread(task)
  }

  val params = Array("username"-> "admin","password"->"123")
  def doPostByProxy() : Thread = {
    val task = new Runnable {
      override def run() : Unit = {
        while(true){
          try{
            val result = httpClient.doPostByProxyWithHttp(
              targetHost,targetPort,proxy,proxyPort,postUri,params)
            logger.info(s"receive data = : $result")
            Thread.sleep(5000)
          }catch {
            case e:SocketException =>
              logger.error("socket error")
              logger.info("retry 10 seconds later")
              Thread.sleep(10000)
          }

        }
      }
    }
    new Thread(task)
  }

  val thread = doGetByProxy()
  thread.start()
  thread.join()

}


