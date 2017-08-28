package entity.request

/**
  * Created by linsixin on 2017/8/25.
  * Represent to the data from client
  * is total encrypted which heads or
  * request line cannot be recognized.
  *
  * When factory or wrapper class meets
  * this class should pass data directly
  * without any process.
  */
class TotalEncryptRequest(data:String) extends Request("",Array.empty,data){

  override def mkHttpString: String = data
}
//case class's parameters are always val
