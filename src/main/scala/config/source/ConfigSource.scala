package config.source

/**
  * Created by linsixin on 2017/9/18.
  */
trait ConfigSource {

  def load() : Map[String,String]
}
