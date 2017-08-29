package entity.request.wrapped

import entity.request.TotalEncryptRequest

/**
  * Created by linsixin on 2017/8/25.
  * This class represents to the requests
  * that cannot recognize anything.
  * That means you cannot handle anything.
  */
class MessRequest(request:TotalEncryptRequest) extends EncryptWrappedRequest(request){

  override def mkHttpString: String = request.mkHttpString
}
