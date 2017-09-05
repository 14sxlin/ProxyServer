package entity.request

/**
  * Created by linsixin on 2017/9/4.
  */
trait Request {
  def mkHttpBinary() : Array[Byte]
}
