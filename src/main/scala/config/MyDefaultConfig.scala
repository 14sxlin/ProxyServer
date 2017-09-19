package config

import config.source.PropertiesFileSource

/**
  * Created by linsixin on 2017/9/18.
  */
object MyDefaultConfig {
  val config = new Config(new PropertiesFileSource("src/config.properties"))
}
