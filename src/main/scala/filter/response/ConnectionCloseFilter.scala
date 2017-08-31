package filter.response
import entity.response.Response
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/31.
  */
object ConnectionCloseFilter extends ResponseFilter{

  override def handle(response: Response):Response = {
    if(response == null || response.headers == null){
      throw new IllegalArgumentException("response should never be null")
    }
    if(response.headers.contains((HttpHeaders.CONNECTION,"close"))){
      response.connectionCloseFlag = true
      response
    }else response
  }
}
