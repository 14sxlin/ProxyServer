package mock.client

import java.net.SocketException

import org.slf4j.LoggerFactory

/**
  * Created by sparr on 2017/8/5.
  */
object HttpClientRun extends App{
  val logger = LoggerFactory.getLogger(getClass)

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
    val task = new Runnable {
      override def run() : Unit = {
        while(true){
          try{
            val result = httpClient.doGetByProxyWithHttp(targetHost, getPort, proxy, proxyPort, targetURI)
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


