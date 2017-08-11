package task

import java.io.PrintWriter

import entity.Response

/**
  * Created by sparr on 2017/8/11.
  */
abstract class Task(val method:String,
                    val uri:String,
                    val httpVersion:String) {
  val onSuccess: (PrintWriter,Response) => {}
  val onFail : (PrintWriter,Response) => {}
  def begin() : Unit
}
