package connection

import constants.LoggerMark
import org.slf4j.LoggerFactory

import scala.util.control.Breaks

/**
  * Created by linsixin on 2017/8/20.
  * CloseWhenNotActive trait will start a thread
  * to check whether connection it's active. Implementation
  * of this trait should use updateActiveTime() method
  * to notify that it's active. Also it needs to implement
  * timeToClose() method to declare what should be close when
  * not active
  */
trait CloseWhenNotActive {

  private val logger = LoggerFactory.getLogger(getClass)

  private var last : Long = System.currentTimeMillis()

  protected val name:String
  protected val resourceName : String

  def timeToClose():Unit

  def updateActiveTime():Unit = {
    last = System.currentTimeMillis()
  }

  import Breaks._
  def closeWhenNotActiveIn(afterSecondClose:Long) : Unit = {
    val monitorRun = new Runnable {
      override def run() : Unit = {
        breakable{
          while(System.currentTimeMillis() - last < afterSecondClose) {
            val left = afterSecondClose-(System.currentTimeMillis() - last)
            if(left > 1000){
              Thread.sleep(left)
            }else break
          }
        }
        logger.info(s"${LoggerMark.resource} $name : $resourceName")
        timeToClose()
      }
    }
    val monitor = new Thread(monitorRun)
    monitor.start()
  }

}
