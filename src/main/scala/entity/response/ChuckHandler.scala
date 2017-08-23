package entity.response

import org.apache.http.HttpHeaders

/**
  * Created by sparr on 2017/8/23.
  */
object ChuckHandler extends ResponseHandler{

  val transferEncoding = HttpHeaders.TRANSFER_ENCODING
  override def handler(response: Response): Response = {
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
