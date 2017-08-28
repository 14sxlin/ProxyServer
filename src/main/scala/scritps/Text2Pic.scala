package scritps

import java.io.{File, FileOutputStream}

import scala.io.Source

/**
  * Created by linsixin on 2017/8/27.
  */
object Text2Pic extends App{
  val pic = "logs/pic/"
  val file = new File(s"$pic/1.txt")
  val data = Source.fromFile(file,"utf-8").mkString.getBytes("utf-8")
  println(s"length = ${data.length}")
  val picFile = new File(s"$pic/1.jpg")
  val out = new FileOutputStream(picFile)
  out.write(data)
  out.flush()
  out.close()
  println(s"save to ${picFile.getAbsoluteFile}")
  println("success !")
}
