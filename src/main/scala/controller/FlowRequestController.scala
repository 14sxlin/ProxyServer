package controller

import java.net.Socket

import config.MyDefaultConfig.config
import connection.pipe.{CacheDataPipe, DataPipe, FilterDataPipe}
import connection.{ClientConnection, ServerConnection}
import constants.ConfigNames
import entity.request.{EmptyRequest, HeaderRecognizedRequest, RequestFactory, TotalEncryptRequest}
import org.apache.commons.lang3.StringUtils
import org.slf4j.{Logger, LoggerFactory}
import utils.{HttpUtils, RequestUtils}


/**
  * Created by linsixin on 2017/9/20.
  */
class FlowRequestController(client: ClientConnection) {


  val logger : Logger = LoggerFactory.getLogger(getClass)
  val readTimeout: Int = config.getInt(ConfigNames.readTimeout)

  def process() : Unit = {
    client.openConnection()
    client.readBinaryData() match {
      case Some(originRequest) =>
        decideHostAndCreateConnection(originRequest) match {
          case Some(server) =>
            startCommunicate(createDataPipe(client,server))
          case None =>
            closeResource(client)
        }
      case None =>
        closeResource(client)
    }
  }

  private def closeResource(clientConnection: ClientConnection): Unit ={
    clientConnection.closeAllResource()
  }


  private def decideHostAndCreateConnection(request:Array[Byte]) : Option[ServerConnection] = {
    RequestFactory.buildRequest(request) match {
      case EmptyRequest => //return
        logger.info("empty able close client")
        client.closeAllResource()
        None
      case _ : TotalEncryptRequest => //ssl
        logger.info("encrypt able,unknown host, close client")
        client.closeAllResource()
        None
      case recognizedRequest :HeaderRecognizedRequest
        if recognizedRequest.method =="CONNECT" =>
        logger.info("start 443 communicate")
        response200AndCreateConnection(recognizedRequest)

      case recognizedRequest : HeaderRecognizedRequest => //post get
        parseHostHeaderAndCreateConnection(request,recognizedRequest)
    }
  }

  private def response200AndCreateConnection(recognizedRequest:HeaderRecognizedRequest) = {
    val uri = recognizedRequest.uri
    val host = uri.split(":").head.trim
    val port = uri.split(":").last.trim.toInt
    val establishInfo = HttpUtils.establishConnectInfo
    client.writeBinaryData(establishInfo.getBytes())
    Some(new ServerConnection(host,port))
  }

  private def parseHostHeaderAndCreateConnection(binaryRequest:Array[Byte],
                                                 recognizedRequest:HeaderRecognizedRequest) = {
    recognizedRequest.getHost match {
      case Some(host) =>
        val port = 80
        val serverSocket = new Socket(host,port)
        val serverCon = new ServerConnection(serverSocket,s"$host:$port")
        serverCon.openConnection()
        val newReq = RequestUtils.updateAbsoluteUriToRelative(recognizedRequest).mkHttpBinary()
        logger.info(s"first request in String: \n${new String(newReq)}")
        serverCon.writeBinaryData(newReq)
        Some(serverCon)
      case None =>
        logger.warn("no host header, close cons")
        client.closeAllResource()
        None
    }
  }

  private def createDataPipe(client: ClientConnection, server: ServerConnection) = {
    client.setReadTimeout(readTimeout)
    server.setReadTimeout(readTimeout)
    client.openConnection()
    server.openConnection()
    //todo
//    new DataPipe(client,server)
//    new CacheDataPipe(client,server)
     new FilterDataPipe(client,server)
  }

  private def startCommunicate(dataPipe: DataPipe) : Unit = {
    try{
      dataPipe.startCommunicate()
    }catch {
      case e:Exception =>
        logger.error(e.getMessage + ".. close connection")
        dataPipe.closeResource()
    }
  }

}
