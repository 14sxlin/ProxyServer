package entity.request.wrapped

import entity.request.TotalEncryptRequest

/**
  * Created by linsixin on 2017/8/25.
  */
class MessRequest(request:TotalEncryptRequest) extends EncryptWrappedRequest(request){

  override def mkHttpString: String = request.mkHttpString
}
