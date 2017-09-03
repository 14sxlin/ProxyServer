package connection

import java.util.concurrent.ArrayBlockingQueue

/**
  * Created by linsixin on 2017/9/1.
  */
object ProduceConsumer extends App{

  val queue = new ArrayBlockingQueue[String](20)
  val consumerRun = new Runnable {
    override def run(): Unit = {
      while(true){
        val str = queue.take()
        println(s"take $str")
        // so strange that it won't output until you stop
        // the progress
      }
    }
  }
  val providerRun = new Runnable {
    override def run(): Unit = {
      while(true){
        val v = Math.random()
        queue.put(""+v)
        println(s"put : $v")
      }
    }
  }

  val consumerThread1 = new Thread(consumerRun);consumerThread1.start()
  val consumerThread2 = new Thread(consumerRun);consumerThread2.start()
  val consumerThread3 = new Thread(consumerRun);consumerThread3.start()

  val proThread = new Thread(providerRun);proThread.start()

  consumerThread1.join()
  proThread.join()
}
