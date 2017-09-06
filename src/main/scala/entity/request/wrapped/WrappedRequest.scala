//package entity.request.wrapped
//
//import entity.body.{BodyEntity, EmptyBody}
//import entity.request.ByteBodyRequest
//import org.apache.commons.lang3.StringUtils
//
///**
//  * Created by linsixin on 2017/8/19.
//  */
//class WrappedRequest(firstLine: String,
//                     headers: Array[(String, String)],
//                     body: String,
//                     val bodyEntity: BodyEntity)
//  extends Request(firstLine, headers, body) {
//  def this(request: Request,
//           bodyEntity: BodyEntity) {
//    this(request.firstLine, request.headers, request.body, bodyEntity)
//  }
//
//  def rawBody: String = {
//    body
//  }
//
//}
//
//object WrappedRequest{
//  val Empty = new WrappedRequest(StringUtils.EMPTY,
//    Array.empty,
//    StringUtils.EMPTY,
//    EmptyBody
//  )
//}
//
