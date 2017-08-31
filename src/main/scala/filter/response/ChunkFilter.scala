package filter.response

import entity.response.{BinaryResponse, Response, TextResponse}
import org.apache.http.HttpHeaders

/**
  * Created by linsixin on 2017/8/23.
  */
object ChunkFilter extends ResponseFilter {

  val transferEncoding = HttpHeaders.TRANSFER_ENCODING
  override def handle(response: Response): Response = {
    if(!response.headers.map(nameValue => nameValue._1 )
                .contains(transferEncoding))
      return response
    response match {
      case r:TextResponse =>
        TextResponse(
          r.firstLine,
          r.headers.filter(_._1 !=transferEncoding ),
          r.body
        )
      case r:BinaryResponse =>
        BinaryResponse(
          r.firstLine,
          r.headers.filter(_._1 !=transferEncoding ),
          r.body
        )
    }

  }
}
