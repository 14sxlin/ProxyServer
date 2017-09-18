package model

import cache.Cacheable
import entity.response.Response

/**
  * Created by linsixin on 2017/9/15.
  */
case class CacheUnit(absoluteUri:String,
                     private val cacheable: Cacheable) {

  private var response:Response = _
  private var filled = false

  def hasFilled : Boolean = filled

  def fill(response: Response): Unit ={
    filled = true
    this.response = response
  }

  def prolong() : Unit = {
    cacheable.prolong()
  }
  def isOutOfDate:Boolean = {
    !cacheable.hasValidateMechanism || cacheable.validate.isOutOfDate
  }

  def getResponse : Response = response

  def getResponseBinary(encoding:String="utf-8"):Array[Byte] = response.mkHttpBinary(encoding)
}
