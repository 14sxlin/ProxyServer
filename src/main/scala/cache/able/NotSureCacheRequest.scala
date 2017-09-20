package cache.able

import cache.validate.Validate

/**
  * Created by linsixin on 2017/9/18.
  */
class NotSureCacheRequest extends Cacheable {

  def toDirectResponseRequest(validate: Validate):DirectResponseRequest = {
    new DirectResponseRequest(validate)
  }

  def toNeedValidateRequest(validate: Validate) : NeedValidateRequest = {
    new NeedValidateRequest(validate)
  }
}
