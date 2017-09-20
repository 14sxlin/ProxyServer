package entity.request

/**
  * Created by linsixin on 2017/8/25.
  * Represent to the data from client
  * is total encrypted which heads or
  * able line cannot be recognized.
  *
  * When factory or wrapper class meets
  * this class should pass data directly
  * without any process.
  */
case class TotalEncryptRequest(bytes:Array[Byte]) extends Request{
  override def mkHttpBinary() = bytes
}
//case class's parameters are always val
