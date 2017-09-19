package config

import config.source.ConfigSource

/**
  * Created by linsixin on 2017/9/18.
  */
class Config(source: ConfigSource) {

  val map:Map[String,String] = source.load()

  def getInt(key:String) : Int = {
    map(key).toInt
  }

  def getBoolean(key:String) : Boolean = {
    map(key).toBoolean
  }

  def get(key:String) : String = {
    map(key)
  }
}
