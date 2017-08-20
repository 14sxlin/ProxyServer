package connection

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by sparr on 2017/8/20.
  */
trait CloseWhenNotActive {

  private val logger = LoggerFactory.getLogger(getClass)

  private var last : Long = System.currentTimeMillis()

  def timeToClose():Unit

  def updateActiveTime():Unit = {
    last = System.currentTimeMillis()
    logger.info(s"active at :$last ..  ")
  }

  def closeWhenNotActiveIn(afterSecondClose:Long) : Unit = {
    val monitorRun = new Runnable {
      override def run() : Unit = {
        while(System.currentTimeMillis() - last < afterSecondClose) {
          val left = afterSecondClose-(System.currentTimeMillis() - last)
          logger.info(s"not ready to close left ${left/1000}s")
          Thread.sleep(left)
        }
        logger.info("time over, close resource")
        timeToClose()
      }
    }
    val monitor = new Thread(monitorRun)
    monitor.start()
  }

}
