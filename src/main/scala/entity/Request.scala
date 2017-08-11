package entity

/**
  * Created by sparr on 2017/8/12.
  */
case class Request(firstLine:String,
                   headers:Array[(String,String)],
                   body:String)
