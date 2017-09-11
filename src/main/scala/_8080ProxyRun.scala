import java.net.SocketException

import http.{ConnectionPoolClient, RequestProxy}
import mock.client.HttpClientMock
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/5.
  */
object _8080ProxyRun extends App{
  val logger = LoggerFactory.getLogger(getClass)

//  val getUri = "http://localhost:8080/LoginDemo/"
  val getPort = 9000
  val proxyPort = 689
  val proxy = "localhost"
  val httpClient = new HttpClientMock
  val demoUrl = "http://localhost:8080/LoginDemo/"
  val postUrl = "http://localhost:8080/LoginDemo/LoginDemo/login.do"
  val getUrl = "http://localhost:8080/LoginDemo/load?time=4"
  val imageUrl = "http://localhost:8080/LoginDemo/pic/1.jpg"
  val jsUrl = "http://localhost:8080/LoginDemo/index.html"

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

  val httpGet = new HttpGet(getUrl)
  val newHttpPooling = new ConnectionPoolClient
  val requestProxy = new RequestProxy(newHttpPooling)
  val httpContext = HttpClientContext.create()
  def doGetByProxyThread(targetURI:String, onSuccess: (String)=>Unit):Thread = {
    val task = new Runnable {
      override def run() : Unit = runAndRetryWhenFail{
        runForTimes(1){
          val result = httpClient.doGetByProxyWithHttp( proxy, proxyPort, targetURI)
          //val proxyRequest = httpClient.buildProxyRequest(proxy, proxyPort,httpGet)
          //httpClient.doRequestProxyInPool(proxyRequest,requestProxy,httpContext)
          onSuccess(result)
          logger.info(s"receive response length = ${result.length}")
        }
      }
    }
    new Thread(task)
  }

  val params = Array("username"-> "admin","password"->"123")
  def doPostByProxyThread() : Thread = {
    val task = new Runnable {
      override def run() : Unit =
        runAndRetryWhenFail {
          val result = httpClient.doPostByProxyWithHttp(
            proxy,proxyPort,postUrl,params)
          logger.info(s"receive request length = ${result.length}")
          Thread.sleep(5000)
        }
    }
    new Thread(task)
  }

  def runAndRetryWhenFail(run : => Unit): Unit ={
    try{
      run
    }catch {
      case e:SocketException =>
        logger.error(s"socket error, ${e.getMessage}")
        logger.info("retry 10 seconds later")
        Thread.sleep(10000)
    }
  }

  def runForTimes(time:Int)(run: => Unit): Unit ={
    for( _ <- 0 until time){
      run
    }
  }
//  val t1 = doGetByProxyThread("/");t1.start()

  val onSuccess = (result:String) =>{
//    val min = Math.min(result.length,200)
    println(result)
  }

  val getSizeUrl = demoUrl + "getSize?size=10000"
  val t2 = doGetByProxyThread(getSizeUrl,onSuccess)
  t2.start()
  t2.join()

//  Thread.sleep(6000)

}


