package cache.able

import cache.validate.Validate

/**
  * Created by linsixin on 2017/9/14.
  * This class represent to the able
  * that you need to cache.validate every time
  * relating to header "Cache-Control:no-cache".
  */
class NeedValidateRequest(mValidate: Validate) extends Cacheable{
  validate = mValidate
}
