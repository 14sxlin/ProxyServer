package connection

/**
  * Created by linsixin on 2017/8/29.
  */
object ConnectionConstants {
  /**
    * how long the connection not active
    * will be regarded as idle
    */
  val idleThreshold = 2000L

  val maxConnection = 200
}
