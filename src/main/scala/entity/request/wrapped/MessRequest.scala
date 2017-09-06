//package entity.request.wrapped
//
///**
//  * Created by linsixin on 2017/8/25.
//  * This class represents to the requests
//  * that cannot recognize anything.
//  * That means you cannot handle anything.
//  */
//class MessRequest(bytes:Array[Byte]) extends EncryptWrappedRequest(bytes){
//
//  override def mkHttpString: String = {
//    s"encrypt data, length:${bytes.length}"
//  }
//}
