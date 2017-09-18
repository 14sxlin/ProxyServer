package constants

/**
  * Created by linsixin on 2017/8/28.
  */
object LoggerMark {
  val up : String = "request "+ ">>"* 5
  val down : String= "response " + "<<"*5
  val process : String= "process "+ "*"*10
  val resource : String= "resource " + "#"*10
  val cache : String = "cache " + "$" * 10
}
