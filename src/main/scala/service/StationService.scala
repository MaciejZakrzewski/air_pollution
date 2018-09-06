package service

import com.softwaremill.sttp._
import model.{City, Commune, Station}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import props.{PropNames, PropUrls}

object StationService {

  def getAllStations: Option[Seq[Station]] = {
    val request = sttp.get(uri"${PropUrls.PROP_GET_ALL_STATIONS_URL}")

    implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

    val response = request.send()

    implicit val communeReads: Reads[Commune] = (
      (JsPath \ PropNames.PROP_COMMUNE_NAME).read[String] and
        (JsPath \ PropNames.PROP_DISTRICT_NAME).read[String] and
        (JsPath \ PropNames.PROP_PROVINCE_NAME).read[String]
      ) (Commune.apply _)

    implicit val cityReads: Reads[City] = (
      (JsPath \ PropNames.PROP_ID).read[Int] and
        (JsPath \ PropNames.PROP_NAME).read[String] and
        (JsPath \ PropNames.PROP_COMMUNE).read[Commune]
      ) (City.apply _)

    implicit val stationsReads: Reads[Station] = (
      (JsPath \ PropNames.PROP_ID).read[Int] and
        (JsPath \ PropNames.PROP_STATION_NAME).read[String] and
        (JsPath \ PropNames.PROP_GEGR_LAT).read[String] and
        (JsPath \ PropNames.PROP_GEGR_LON).read[String] and
        (JsPath \ PropNames.PROP_GEGR_ADDRESS_STREET).readNullable[String] and
        (JsPath \ PropNames.PROP_CITY).read[City]

      ) (Station.apply _)

    val json: JsValue = Json.parse(response.unsafeBody)

    json.validate[Seq[Station]] match {
      case s: JsSuccess[Seq[Station]] =>
        val stations: Seq[Station] = s.get
        Some(stations)
      case _: JsError =>
        None
    }
  }
}

