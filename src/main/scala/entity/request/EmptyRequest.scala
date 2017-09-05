package entity.request

/**
  * Created by linsixin on 2017/9/4.
  */
object EmptyRequest extends Request{
  override def mkHttpBinary() = Array.empty[Byte]
}
