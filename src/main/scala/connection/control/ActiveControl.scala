package connection.control

import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/29.
  * This trait means that some thing
  * cannot record its' active time.
  */
trait ActiveControl {

  private val logger = LoggerFactory.getLogger(getClass)

  protected val idleThreshold : Long

  protected var last : Long = System.currentTimeMillis()

  def closeWhenNotActive():Unit

  def updateActiveTime():Unit = {
    this.synchronized{
      last = System.currentTimeMillis()
    }
    logger.info(s"active at $last")
  }

  def isIdle : Boolean = {
    System.currentTimeMillis() - last > idleThreshold
  }

}
