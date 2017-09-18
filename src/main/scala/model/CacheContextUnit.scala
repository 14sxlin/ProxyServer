package model

import connection.ClientConnection
import constants.LoggerMark
import entity.response.Response
import filter.ResponseFilterChain
import org.apache.http.client.protocol.HttpClientContext
import org.slf4j.LoggerFactory

/**
  * Created by linsixin on 2017/9/16.
  */
class CacheContextUnit(override val clientConnection: ClientConnection,
                       override val context:HttpClientContext,
                       val cacheUnit: CacheUnit) extends ContextUnit(clientConnection,context){
}
