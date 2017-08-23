package connection

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by linsixin on 2017/8/20.
  * CloseWhenNotActive trait will start a thread
  * to check whether some it's active. Implementation
  * of this trait should use updateActiveTime() method
  * to notify that it's active. Also it needs to implement
  * timeToClose() method to declare what should be close when
  * not active
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
