package service

import com.typesafe.scalalogging.Logger
import model.AirQualityIndex
import org.slf4j.LoggerFactory
import props.PropUrls

object AirQualityIndexesService {
  private val LOG: Logger = Logger(LoggerFactory.getLogger(AirQualityIndexesService.getClass))


//  def getAirQualityIndex(stationId: Int): Option[AirQualityIndex] = {
//    LOG.debug("Getting air quality index for station: {}", stationId)
//
//    val url = PropUrls.PROP_GET_AIR_QUALITY_IDX_URL + stationId
//
//    parseAirQualityJson(ApiSenderService.getDataFromApi(url))
//  }

//  private[this] def parseAirQualityJson(airQualityJson: String): Option[AirQualityIndex] = {
//    implicit val airQualityReads: Reads[]
//  }

}
