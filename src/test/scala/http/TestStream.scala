package http

import java.io.{File, FileOutputStream}

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

/**
  * Created by sparr on 2017/8/23.
  */
object TestStream extends App {

  val url = "http://localhost:8080/LoginDemo/pic/1.jpg"
  val client = HttpClients.createDefault()

  val httpGet = new HttpGet(url)

  val response = client.execute(httpGet)
  val entity = response.getEntity

  def receiveRecursicvely() = {
    val in = entity.getContent
    val data = ArrayBuffer[Byte]()
    val buffer = new Array[Byte](1024)
    @tailrec def read() : Unit = {
      val length = in.read(buffer)
      if(length == -1)
        return
      data ++= buffer.slice(0,length)
      println(s"read data: \n${new String(buffer)} \n")
      if(length == buffer.length)
        read()
    }
    read()
//    println(s"total : \n${new String(buffer)} \n\n length = ${data.length}")
    in.close()

    val file = new File("logs/1.jpg")
    val out = new FileOutputStream(file)
    out.write(data.toArray)
    out.close()

  }

  def receiveWhile() = {
    val in = entity.getContent
    val total = new ArrayBuffer[Byte]()
    val buffer = new Array[Byte](1024)
    var length = in.read(buffer)
    while(length != -1){
      val line = buffer.slice(0,length)
      println(s"line = ${new String(line,"utf-8")}")
      total ++= line
      length = in.read(buffer)
    }
    println(s"data : ${new String(total.toArray,"utf-8")}")
    in.close()
  }

  def receiveToString() = {
    println(EntityUtils.toString(response.getEntity,"utf-8"))
  }

  receiveRecursicvely()
//  receiveWhile()



  response.close()
  client.close()

}
