package connection

/**
  * Created by sparr on 2017/8/20.
  */
class DataTransfer(client:ClientConnection,
                   server:ServerConnection) {

  def transferOnce():Int = {
    val data = client.readBinaryData()
    server.writeBinaryData(data)
    data.length
  }

}
