package entity.request

import entity.body.EncryptData

/**
  * Created by sparr on 2017/8/20.
  */
class EncryptWrappedRequest(request: Request)
        extends WrappedRequest(
          request.firstLine,
          request.headers,request.body,
          EncryptData(request.body)){
}
