package utils.http

import java.io.{File, FileOutputStream, PrintWriter}

/**
  * Created by linsixin on 2017/8/28.
  */
object FileUtils {

  def save2File(path:String,data:Array[Byte]) ={
    val file = new File(path)
    val out = new FileOutputStream(file)
    out.write(data)
    out.flush()
    out.close()
  }

  def save2File(path:String,data:String) ={
    val file = new File(path)
    val out = new PrintWriter(file)
    out.write(data)
    out.flush()
    out.close()
  }
}
