package config.source

import scala.io.Source

/**
  * Created by linsixin on 2017/9/18.
  */
class FileSource(file: String) extends ConfigSource {

  override def load(): Map[String, String] = {
    var map = collection.mutable.Map[String, String]()
    Source.fromFile(file).getLines().foreach {
      line =>
        lineToMap(line) match {
          case Some(elem) => map += elem
          case None =>
        }
    }
    map.toMap
  }

  private def lineToMap(line: String): Option[(String, String)] = {
    if (line == null || line.trim.isEmpty)
      return None
    val parts = line.split("=")
    assert(parts.length == 2)
    val name = parts(0).trim
    val value = parts(1).trim
    Some(name -> value)
  }
}
