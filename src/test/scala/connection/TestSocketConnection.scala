package connection

import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.{HttpURLConnection, Socket, URI, URL}

import org.apache.http.client.methods.HttpGet
import org.scalatest.FunSuite
import utils.{FileUtils, HttpUtils}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by linsixin on 2017/9/20.
  */
class TestSocketConnection extends FunSuite{

  test("socket connection "){
    //    val request = "GET /adv/producer/sendLog?timestamp=1505902003938&domain=www.sohu.com&source_agent=3&referer_url=http%3A%2F%2Fwww.sohu.com%2F&user_ecoid=-1&user_suv=1708202100380746&city_id=199&area_code=440500&type=2 HTTP/1.1\nHost: adv-sv-stat.focus.cn\nProxy-Connection: keep-alive\nPragma: no-cache\nCache-Control: no-cache\nAccept: image/webp,image/*,*/*;q=0.8\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.1.3397.16 Safari/537.36\nReferer: http://www.sohu.com/\nAccept-Encoding: gzip, deflate\nAccept-Language: zh-CN,zh;q=0.8\n\n"
    //    val host = "adv-sv-stat.focus.cn"
    val request = "GET /s?z=cathay&c=87 HTTP/1.1\nHost: galaxy.bjcathay.com\nConnection: keep-alive\nPragma: no-cache\nCache-Control: no-cache\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\nUpgrade-Insecure-Requests: 1\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.1.3397.16 Safari/537.36\nAccept-Encoding: gzip, deflate\nAccept-Language: zh-CN,zh;q=0.8\nCookie: ASL=17429,000zk,a3cc077ab7e9e329; ADVC=3572cb3e90e383; ADVS=35802b83b7d93f\n\n"
    val host = "galaxy.bjcathay.com"
    socketConnect(host,request)
  }

  test("socket connection 1 "){ //403
    val request = "GET http://cms-bucket.nosdn.127.net/2c7c48025ed3447f83a7fbacbd6143ef20170920170135.jpeg?imageView&thumbnail=200y125&quality=85 HTTP/1.1\nHost: cms-bucket.nosdn.127.net\nProxy-Connection: keep-alive\nPragma: no-cache\nCache-Control: no-cache\nAccept: image/webp,image/*,*/*;q=0.8\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.1.3397.16 Safari/537.36\nReferer: http://www.163.com/\nAccept-Encoding: gzip, deflate\nAccept-Language: zh-CN,zh;q=0.8\n\n"
    val host = "cms-bucket.nosdn.127.net"
    socketConnect(host,request)
  }

  test("socket con 2") {
    val req = "GET /447cfa10566b42309de649d5d326bfa520170901150037.jpeg?imageView&thumbnail=185y116&quality=85 HTTP/1.1\nHost: cms-bucket.nosdn.127.net\nProxy-Connection: keep-alive\nAccept: image/webp,image/*,*/*;q=0.8\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.1.3397.16 Safari/537.36\nReferer: http://www.163.com/\nAccept-Encoding: gzip, deflate\nAccept-Language: zh-CN,zh;q=0.8\n\n"
    val host = "cms-bucket.nosdn.127.net"
    socketConnect(host,req)
  }

  def socketConnect(host:String,request:String) = {
    val port = 80
    val socket = new Socket(host,port)
    val data = new Array[Byte](1024)
    val out = new BufferedOutputStream(socket.getOutputStream)
    out.write(request.getBytes())
    out.flush()
    val in = new BufferedInputStream(socket.getInputStream)
    var total = ArrayBuffer[Byte]()
    var length = 0
    while(length != -1){
      length = in.read(data)
      total ++= data.slice(0,length)
      println(new String(data.slice(0,length)))
    }
    FileUtils.save2File("logs/pic/test2.jpeg",total.toArray)
    out.close()
    in.close()
    socket.close()
  }

  test("http get"){
    val get = "http://s.go.sohu.com/adgtr/?callback=sjs_1725877040393424&itemspaceid=15319&adps=20000002&adsrc=13&turn=1&sf=1&pgid=acbd658e-f899-9fd5-2bf7-4564c9f68e77&newschn=1000000000"
    val response = HttpUtils.execute(new HttpGet(get))
    print(response.mkHttpString())
  }

  test("http get connection"){
    val url = new URL("http://s.go.sohu.com/adgtr/?callback=sjs_1725877040393424&itemspaceid=15319&adps=20000002&adsrc=13&turn=1&sf=1&pgid=acbd658e-f899-9fd5-2bf7-4564c9f68e77&newschn=1000000000")
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setDoOutput(true)
    connection.connect()
    val data = new Array[Byte](1024)
//    val out = new BufferedOutputStream(connection.getOutputStream)
//    out.write(request.getBytes())
//    out.flush()
    val in = new BufferedInputStream(connection.getInputStream)
    val length = in.read(data)
    println(new String(data.slice(0,length)))

    connection.disconnect()
//    out.close()
    in.close()
  }

}
