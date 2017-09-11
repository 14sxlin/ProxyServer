package utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

/**
  * Created by linsixin on 2017/9/9.
  */
object GZipUtils {


  def encode(content:String, encoding:String="utf8"):Array[Byte] = {
    encode(content.getBytes())
  }

  def encode(content:Array[Byte]) : Array[Byte] = {
    val compressContent = new ByteArrayOutputStream()
    val gzipOut = new GZIPOutputStream(compressContent)
    gzipOut.write(content)
    gzipOut.finish()
    gzipOut.close()
    val compressByte = compressContent.toByteArray
    compressContent.close()
    compressByte
  }

  def decode(compressByte:Array[Byte]) : Array[Byte] = {
    val compressIn = new ByteArrayInputStream(compressByte)
    val gzipIn = new GZIPInputStream(compressIn)

    val decompressContent = new ByteArrayOutputStream()
    val buffer = new Array[Byte](1024)
    var length = gzipIn.read(buffer)
    while(length != -1) {
      decompressContent.write(buffer.slice(0,length))
      length = gzipIn.read(buffer)
    }
    decompressContent.toByteArray
  }

}
