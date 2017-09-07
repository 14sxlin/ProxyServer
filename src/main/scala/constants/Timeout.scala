package constants

/**
  * Created by linsixin on 2017/9/2.
  */
object Timeout {
  /**
    *  get connection from pool timeout
    */
  val connectionRequestTimeout = 5000
  /**
    * connect to socket time out
    */
  val connectTimeout = 5000
  /**
    * read data from socket time out
    */
  val readTimeout = 10000
}
