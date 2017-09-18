package cache

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import org.scalatest.FunSuite

/**
  * Created by linsixin on 2017/9/17.
  */
class TestSerialize extends FunSuite{
  test("serilize"){
    val toSerialize = new MaxAgeValidate("some thing")
    val buffer = new ByteArrayOutputStream()
    val objOut = new ObjectOutputStream(buffer)
    objOut.writeObject(toSerialize)
    objOut.close()

    val objIn = new ByteArrayInputStream(buffer.toByteArray)
    val recover = new ObjectInputStream(objIn).readObject()
    assert(recover.isInstanceOf[MaxAgeValidate])
    val maxAgeValidate = recover.asInstanceOf[MaxAgeValidate]
    println(""+maxAgeValidate.maxAge)
    assert(toSerialize.maxAge == maxAgeValidate.maxAge)

  }

}
