package http

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/9/9.
  */
class TestGZip extends FunSuite{

  test("gzip compress and decompress ") {
    val content = "123123123123123123123123123123123".getBytes()
    val compressContent = new ByteArrayOutputStream()
    val gzipOut = new GZIPOutputStream(compressContent)
    gzipOut.write(content)
    gzipOut.finish()
    gzipOut.close()

    val compressByte = compressContent.toByteArray
    compressContent.close()
    println(s"before : ${new String(content)}")
    println(s"after  : ${new String(compressByte)}")
    assert(compressByte.length < content.length)

    val compressIn = new ByteArrayInputStream(compressByte)
    val gzipIn = new GZIPInputStream(compressIn)

    val decompressContent = new ByteArrayOutputStream()
    val buffer = new Array[Byte](1024)
    var length = gzipIn.read(buffer)
    while(length != -1) {
      decompressContent.write(buffer.slice(0,length))
      length = gzipIn.read(buffer)
    }
    val decompress = new String(decompressContent.toByteArray)
    println(s"after  : $decompress")
    assert(new String(content) == decompress )
    decompressContent.close()
    gzipIn.close()
  }
}
