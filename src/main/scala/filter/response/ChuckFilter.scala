package filter.response

import entity.response.Response
import org.apache.http.HttpHeaders

/**
  * Created by sparr on 2017/8/23.
  */
object ChuckFilter extends ResponseFilter {

  val transferEncoding = HttpHeaders.TRANSFER_ENCODING
  override def handle(response: Response): Response = {
    if(!response.headers.map(nameValue => nameValue._1 )
                .contains(transferEncoding))
      return response
    Response(
      response.firstLine,
      response.headers.filter(_._1 !=transferEncoding ),
      response.body
    )

  }
}
