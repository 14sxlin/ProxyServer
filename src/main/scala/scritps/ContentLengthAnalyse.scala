package scritps

import scala.io.Source

/**
  * Created by linsixin on 2017/8/27.
  */
object ContentLengthAnalyse extends App {
  val path = "E:/1.txt"
  val source = Source.fromFile(path)

  val str = source.getLines().mkString("\r\n")
  println(str)

  println(s"length = ${str.getBytes.length}")
}
