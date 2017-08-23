package scritps

import java.io.{File, PrintWriter}

import scala.io.Source

/**
  * Created by sparr on 2017/8/21.
  */
object LogAnalyse extends App{
  val desktopLog = "C:\\Users\\sparr\\Desktop\\log"
  val src = new File(s"logs/log.txt")
  val prefix = "[Thread-"
  val mark = "1]"



  /**
    *
    * @param prefix same parts in different thread
    * @param mark different parts in different thread
    */
  def extract(prefix:String,mark:String):Unit = {
    val target = new File(s"$desktopLog\\$prefix$mark.txt")
    val out = new PrintWriter(target)
    val source = Source.fromFile(src)

    var shouldTake = false
    for( line <- source.getLines()){
      if(line contains prefix){
        if(line contains prefix+mark){
          out.append(line+"\n")
          shouldTake = true
        }else shouldTake = false
      }else if(shouldTake) out.append(line+"\n")
    }
    out.flush()
    out.close()
    source.close()
  }
  for( m <- 1 to 100)
    extract(prefix,s"$m]")
  println("success...")
}
