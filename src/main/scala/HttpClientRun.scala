import java.io.{File, FileOutputStream}
import java.net.SocketException

import entity.response.TextResponse
import mock.client.HttpClientMock
import org.slf4j.LoggerFactory
import utils.http.HexUtils

/**
  * Created by linsixin on 2017/8/5.
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

  def doGetByProxy(targetURI:String,onSuccess: (String)=>Unit):Thread = {
    @volatile var success = false
    val task = new Runnable {
      override def run() : Unit = {
        while(!success){
          try{
            val result = httpClient.doGetByProxyWithHttp(targetHost, 8080, proxy, proxyPort, targetURI)
            logger.info(s"receive data length = ${result.length}")
            onSuccess(result)
            success = true
          }catch {
            case e:SocketException =>
              logger.error(s"socket error, ${e.getMessage}")
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
            logger.info(s"receive data length = ${result.length}")
            Thread.sleep(5000)
          }catch {
            case e:SocketException =>
              logger.error(s"socket error ${e.getMessage}")
              logger.info("retry 10 seconds later")
              Thread.sleep(10000)
          }

        }
      }
    }
    new Thread(task)
  }

//  val t1 = doGetByProxy("/");t1.start()

  val onSuccess = (result:String) =>{
    println(result)
  }

  val t2 = doGetByProxy("/LoginDemo/pic/1.jpg",onSuccess);t2.start()
//  t1.join()
  t2.join()

}


