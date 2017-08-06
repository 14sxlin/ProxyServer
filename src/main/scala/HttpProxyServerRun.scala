import java.util.concurrent.TimeUnit

import http.HttpProxyServer
import org.slf4j.LoggerFactory

import scala.concurrent.forkjoin.ForkJoinPool

/**
  * Created by sparr on 2017/8/1.
  */
object HttpProxyServerRun extends App{

  val logger = LoggerFactory.getLogger(getClass)

  val executePool = new ForkJoinPool(4)

  val task = new Runnable {
    override def run() : Unit = {
      while(true){
        logger.info("new server waiting ..")
        val server = new HttpProxyServer
        server.accept()
      }
    }
  }
  logger.info("start running ..")

  executePool.execute(task)
//
  executePool.awaitTermination(10,TimeUnit.MINUTES)

//  val server = new HttpProxyServer
//  server.accept()

  logger.info("time out .. quit")
}
