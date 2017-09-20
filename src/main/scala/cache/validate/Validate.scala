package cache.validate

import java.time.LocalDateTime

/**
  * Created by linsixin on 2017/9/14.
  */
trait Validate extends Serializable{

  protected var createTime: LocalDateTime = LocalDateTime.now()

  def updateCreateTime() : Unit = {
    createTime = LocalDateTime.now()
  }

  def prolongDelay():Unit
  def fill(info:String):Unit
  def isOutOfDate : Boolean
}
