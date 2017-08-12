package handler.header

/**
  * Created by linsixin on 2017/8/12.
  * Get default header handler chain
  */
object DefaultHeaderChain {

  val proxyHeaderHandler = new ProxyHeaderHandler

  def firstOfRequestHeaderHandlerChain : HeaderHandler ={
    proxyHeaderHandler
  }

  def firstOfResponseHeaderHandlerChain : HeaderHandler = {
    proxyHeaderHandler
  }

}
