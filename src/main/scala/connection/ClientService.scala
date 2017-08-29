package connection

import entity.request.dispatch.RequestSession

/**
  * Created by linsixin on 2017/8/29.
  */
class ClientService(clientConnection: ClientConnection,
                    requestSession: RequestSession) extends ServiceUnit{

  override protected val idleThreshold: Long = ConnectionConstants.idleThreshold




  override def timeToClose(): Unit = {
    clientConnection.closeAllResource()
  }
}
