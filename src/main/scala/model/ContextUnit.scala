package model

import connection.ClientConnection
import connection.control.ActiveControl
import constants.ConnectionConstants
import org.apache.http.client.protocol.HttpClientContext

/**
  * Created by linsixin on 2017/8/29.
  *
  * This class holds the http context for
  * a period of requests.And also the client
  * connection which will be used to response.
  */
case class ContextUnit(clientConnection: ClientConnection,
                       context:HttpClientContext) extends ActiveControl{

  override protected val idleThreshold: Long = ConnectionConstants.idleThreshold

  override def closeWhenNotActive(): Unit = {
    clientConnection.closeAllResource()
  }
}
