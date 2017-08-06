package log

import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

/**
  * Created by sparr on 2017/7/30.
  * just test slf4j api
  */
class TestSLF4J extends  FunSuite{

  test("test SLF4j"){
    val logger = LoggerFactory.getLogger(getClass)
    logger.debug("full of shit")
  }
}
