package utils

import java.io.InputStream

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

/**
  * Created by linsixin on 2017/8/28.
  */
object IOUtils {

  def dataFromResponseInputStream(in:InputStream) : Array[Byte]= {
    val buffer = new Array[Byte](10240)
    val data = new ArrayBuffer[Byte]()
    @tailrec
    def readIn() : Unit = {
      val len = in.read(buffer)
      if(len == -1) return
      data ++= buffer.slice(0,len)
      readIn()
    }
    readIn()
    data.toArray
  }

}
