package connection.control

import connection.pool.ContextUnitPool
import model.ContextUnit
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/8/29.
  */
class IdleServiceGCThread[T <: ContextUnit](val conPool:ContextUnitPool[T]) extends Thread {

  private val logger = LoggerFactory.getLogger(getClass)

  private val gcPeriod = 5000L

  setDaemon(true)

  override def run():Unit = {
    var zeroCount = 0
    this.synchronized{
      while(true){
        val gcCount = conPool.removeIdleServiceUnit()
        logger.info(s"close $gcCount idle connection in pool,zeroCount:($zeroCount)")
        if( gcCount == 0){
          zeroCount += 1
          if(zeroCount > 10)
            zeroCount = 5
          wait(gcPeriod + 500 * zeroCount)
        }else{
          zeroCount = 0
          wait(gcPeriod)
        }
      }
    }
  }

}
