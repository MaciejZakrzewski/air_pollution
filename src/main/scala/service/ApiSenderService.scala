package service

import com.softwaremill.sttp.{Id, _}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object ApiSenderService {

  private val LOG: Logger = Logger(LoggerFactory.getLogger(ApiSenderService.getClass))

  def getDataFromApi(url: String): String = {
    val request = sttp.get(uri"$url")

    implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

    LOG.debug("Sending request: {}", url)

    val response = request.send()

    response.unsafeBody
  }

}
