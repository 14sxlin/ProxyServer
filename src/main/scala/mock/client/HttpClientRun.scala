package mock.client

import java.net.SocketException

import org.slf4j.LoggerFactory

/**
  * Created by sparr on 2017/8/5.
  */
object HttpClientRun extends App{
  val logger = LoggerFactory.getLogger(getClass)


  val proxyPort = 689
  val proxy = "localhost"
  val targetURI = "/"
  val targetPort = 9000
  val targetHost = "localhost"
  val httpClient = new HttpClientMock

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


  def post(url:String): Thread = {
    val doPost = new Runnable {
      override def run(): Unit = {
        val attrs = Array("wd")
        val vals =  Array("书籍")
        new HttpClientMock().doPost(url,attrs,vals)
      }

    }
    val doPostThread = new Thread(doPost)
    doPostThread.start()
    doPostThread
  }

  def getHttpClient() = {

  }

//  post().join()
//  get(localhost).join()

  def postByProxy : Thread = {
    val task = new Runnable {
      override def run() : Unit = {
        while(true){
          try{
            val result = httpClient.doGetByProxyWithHttp(targetHost,targetPort,proxy,proxyPort,targetURI)
            logger.info(s"receive data length = : ${result.length}")
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

  val thread = postByProxy
  thread.start()
  thread.join()

}


