package connection.control

import connection.ConnectionConstants

/**
  * Created by linsixin on 2017/8/29.
  */
class IdleConnectionThread(val conPool:ConnectionPool) extends Thread {

  private val gcPeriod = ConnectionConstants.idleThreshold + 1000

  override def run():Unit = {
    this.synchronized{
      while(true){
        var zeroCount = 0
        if(conPool.closeIdleConnection() == 0){
          zeroCount += 1
          wait(gcPeriod + 500 * zeroCount)
        }else{
          zeroCount = 0
          wait(gcPeriod)
        }
      }
    }
  }

  def doGarbageCollection() :Unit = {
    this.synchronized{
      notify()
    }
  }

}
