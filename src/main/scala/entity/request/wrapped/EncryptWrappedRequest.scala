package entity.request.wrapped

import entity.body.EncryptData
import entity.request.Request

/**
  * Created by linsixin on 2017/8/20.
  * This class represent the request
  * that can recognize the request line
  * and headers but cannot recognize the
  * body
  */
class EncryptWrappedRequest(request: Request)
        extends WrappedRequest(
          request.firstLine,
          request.headers,request.body,
          EncryptData(request.body)){
}
