package service

import com.typesafe.scalalogging.Logger
import model.{AirQualityIndex, IndexLevel}
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.api.libs.functional.syntax._
import props.{PropNames, PropUrls}

object AirQualityIndexesService {
  private val LOG: Logger = Logger(LoggerFactory.getLogger(AirQualityIndexesService.getClass))


  def getAirQualityIndex(stationId: Int): Option[AirQualityIndex] = {
    LOG.debug("Getting air quality index for station: {}", stationId)

    val url = PropUrls.PROP_GET_AIR_QUALITY_IDX_URL + stationId

    parseAirQualityJson(ApiSenderService.getDataFromApi(url))
  }

  private[this] def parseAirQualityJson(airQualityJson: String): Option[AirQualityIndex] = {
    implicit val indexLevelReads: Reads[IndexLevel] = (
      (JsPath \ PropNames.PROP_ID).read[Int] and
        (JsPath \ PropNames.PROP_INDEX_LEVEL_NAME).read[String]
    ) (IndexLevel.apply _)

    implicit val airQualityReads: Reads[AirQualityIndex] = (
      (JsPath \ PropNames.PROP_ID).read[Int] and
        (JsPath \ PropNames.PROP_INDEX_ST_INDEX_LEVEL).readNullable[IndexLevel] and
        (JsPath \ PropNames.PROP_INDEX_SO2_INDEX_LEVEL).readNullable[IndexLevel] and
        (JsPath \ PropNames.PROP_INDEX_NO2_INDEX_LEVEL).readNullable[IndexLevel] and
        (JsPath \ PropNames.PROP_INDEX_CO_INDEX_LEVEL).readNullable[IndexLevel] and
        (JsPath \ PropNames.PROP_INDEX_PM10_INDEX_LEVEL).readNullable[IndexLevel] and
        (JsPath \ PropNames.PROP_INDEX_PM25_INDEX_LEVEL).readNullable[IndexLevel] and
        (JsPath \ PropNames.PROP_INDEX_O3_INDEX_LEVEL).readNullable[IndexLevel] and
        (JsPath \ PropNames.PROP_INDEX_C6H6_INDEX_LEVEL).readNullable[IndexLevel]
    ) (AirQualityIndex.apply _)

    val json: JsValue = Json.parse(airQualityJson)

    json.validate[AirQualityIndex] match {
      case s: JsSuccess[AirQualityIndex] =>
        val airQualityIndex: AirQualityIndex = s.get

        LOG.debug("Returning air quality index")

        Some(airQualityIndex)
      case _: JsError =>
        LOG.debug("Parsing error")

        None
    }
  }

}
