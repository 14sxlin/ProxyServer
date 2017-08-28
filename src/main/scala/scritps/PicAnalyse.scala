package scritps

import java.io.{File, FileInputStream, FileOutputStream}

import utils.http.{FileUtils, HexUtils}

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * Created by linsixin on 2017/8/27.
  */
object PicAnalyse extends App{
  private def txt2Pic() ={
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

  def pic2Txt(pic:String) = {
    val file = new File(pic)
    val in = new FileInputStream(file)
    val total = ArrayBuffer[Byte]()
    var totalLen = 0
    val buffer = new Array[Byte](1024)

    @tailrec
    def readUntilBufferNotFull():Unit = {
      val length = in.read(buffer)
      if(length == -1){
        return
      }
      total ++= buffer.slice(0, length)
      totalLen += length
      if(length == buffer.length)
        readUntilBufferNotFull()
    }
    readUntilBufferNotFull()

//    println(HexUtils.toHex(total.toArray))
//
//    println(picText)
    //    println(total.length)
    val picText = new String(total.toArray,"gbk")
    val shitData = picText.getBytes("gbk")
    val shitPic = HexUtils.toHex(shitData)
    val okPic = HexUtils.toHex(total.toArray)
    FileUtils.save2File("logs/shit.txt",shitPic)
    FileUtils.save2File("logs/ok.txt",okPic)
    FileUtils.save2File("logs/new-demo.jpg",total.toArray)
    FileUtils.save2File("logs/shit-demo.jpg",shitData)
    assert(shitPic != okPic)
  }

//  pic2Txt("logs/demo.jpg")

}
