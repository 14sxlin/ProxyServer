package task

import java.io.PrintWriter

import entity.Response

/**
  * Created by sparr on 2017/8/12.
  */
class GetTask(override val method:String,
              override val uri:String,
              override val httpVersion:String)
                  extends Task(method,uri,httpVersion){

  override val onSuccess: (PrintWriter, Response) => {} =
    (writer,response) =>{
???
    }

  override val onFail: (PrintWriter, Response) => {} =
    (writer,response) =>{
      ???
    }

  override def begin(): Unit = {
    ???
  }
}
