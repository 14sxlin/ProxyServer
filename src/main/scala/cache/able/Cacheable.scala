package cache.able

import cache.validate.Validate

import scala.collection.mutable.ArrayBuffer

/**
  * Created by linsixin on 2017/9/14.
  */
trait Cacheable extends Serializable{

  var validate : Validate = _
  var response = new ArrayBuffer[Array[Byte]]()
  var filled = false

  def hasValidateMechanism : Boolean = validate != null

  def isOutOfDate : Boolean = {
    if(hasValidateMechanism)
      validate.isOutOfDate
    else true
  }

  def prolong():Unit = {
    if(!hasValidateMechanism)
      throw new IllegalStateException("no cache.validate machanism")
    else
      validate.prolongDelay()
  }

  def addResponsePart(part:Array[Byte]): Unit ={
    response += part
  }

  def finish(): Unit ={
    filled = true
  }

  def isFilled : Boolean = filled


  def responsePartsForeach(run : Array[Byte] => Unit): Unit ={
    response foreach run
  }

}
