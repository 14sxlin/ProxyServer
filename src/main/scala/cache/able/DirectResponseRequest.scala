package cache.able

import cache.validate.Validate

/**
  * Created by linsixin on 2017/9/14.
  * Request which allow to response
  * directly if response is not expire.
  */
class DirectResponseRequest(mValidate: Validate) extends Cacheable{
  validate = mValidate
}
