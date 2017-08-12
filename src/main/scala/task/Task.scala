package task

import java.io.PrintWriter

import entity.{Request, Response}
import org.slf4j.LoggerFactory

/**
  * Created by sparr on 2017/8/11.
  */
abstract class Task(request:Request) {
  protected val logger = LoggerFactory.getLogger(getClass)

  var onSuccess: (Response) => {}
  var onFail : (Exception) => {} = e => e
  def begin() : Unit
}
