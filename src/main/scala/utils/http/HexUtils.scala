package utils.http

/**
  * Created by sparr on 2017/8/22.
  */
object HexUtils{

  def toHex(bytes:Array[Byte]) : String = {
    val buffer = new StringBuffer()
    var count = 0
    def wiresharkSpace = {
      count += 1
      if(count % 16 == 0) "\n"
      else if(count % 8 == 0) " "
      else ""
    }

    bytes.foreach(byte =>{
      byte & 0xFF match {
        case x if x<16 =>
          buffer.append(s"0${Integer.toHexString(x)} $wiresharkSpace")
        case x =>
          buffer.append(s"${Integer.toHexString(x)} $wiresharkSpace")
      }
    })
    buffer.toString
  }
//  println(string)

}
