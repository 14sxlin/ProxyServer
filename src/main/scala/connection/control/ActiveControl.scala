package connection.control

import constants.LoggerMark
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/29.
  * This trait means that some thing
  * cannot record its' active time.
  */
trait ActiveControl {

  protected val idleThreshold : Long

  private var last : Long = System.currentTimeMillis()

  private val logger = LoggerFactory.getLogger(getClass)
  private var hasIdle = false

  def closeWhenNotActive():Unit

  def updateActiveTime():Unit = {
    this.synchronized{
      if(hasIdle)
        return
      last = System.currentTimeMillis()

    }
    logger.info(s"active at $last")
  }

  def isIdle : Boolean = {
    this.synchronized{
      if(!hasIdle){
        val period = System.currentTimeMillis() - last
        logger.info(s"${LoggerMark.process} from last : ${period/1000} > ${idleThreshold/1000} ? then idle")
        hasIdle = period > idleThreshold
        hasIdle
      }else hasIdle
    }
  }

}
