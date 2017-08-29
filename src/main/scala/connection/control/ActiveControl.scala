package connection.control

/**
  * Created by linsixin on 2017/8/29.
  * This trait means that some thing
  * cannot record its' active time.
  */
trait ActiveControl {

  protected val idleThreshold : Long

  protected var last : Long = System.currentTimeMillis()

  def timeToClose():Unit

  def updateActiveTime():Unit = {
    this.synchronized{
      last = System.currentTimeMillis()
    }
  }

  def isIdle : Boolean = {
    System.currentTimeMillis() - last > idleThreshold
  }

}
