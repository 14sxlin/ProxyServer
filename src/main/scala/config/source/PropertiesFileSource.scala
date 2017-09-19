package config.source

import java.io.{File, FileInputStream}
import java.util.Properties

import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/9/18.
  */
class PropertiesFileSource(file:String) extends ConfigSource{

  private val logger = LoggerFactory.getLogger(getClass)

  override def load():Map[String,String] = {
    logger.info(s"load : $file")
    val propertiesFile = new File(file)
    if(!propertiesFile.exists() || propertiesFile.isDirectory){
      logger.warn("file not found")
      Map.empty[String,String]
    }else{
      val properties = new Properties
      val in = new FileInputStream(file)
      properties.load(in)
      val mutableMap = collection.mutable.Map[String,String]()
      val names = properties.propertyNames()
      while(names.hasMoreElements){
        val name = names.nextElement()
        val value = properties.get(name).toString.trim
        logger.info(s"load : $name $value")
        mutableMap += name.toString.trim -> value
      }
      in.close()
      mutableMap.toMap
    }
  }

}
