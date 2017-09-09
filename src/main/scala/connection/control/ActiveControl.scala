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

  private var hasIdle = false

  def closeWhenNotActive():Unit

  def updateActiveTime():Unit = {
    this.synchronized{
      if(hasIdle)
        return
      last = System.currentTimeMillis()

    }
  }

  def isIdle : Boolean = {
    this.synchronized{
      if(!hasIdle){
        val period = System.currentTimeMillis() - last
        hasIdle = period > idleThreshold
        hasIdle
      }else hasIdle
    }
  }

}
