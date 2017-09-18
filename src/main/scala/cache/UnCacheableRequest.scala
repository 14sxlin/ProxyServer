package cache

/**
  * Created by linsixin on 2017/9/18.
  */
class UnCacheableRequest private() extends Cacheable{
}

object UnCacheableRequest {
  val instance = new UnCacheableRequest
}
