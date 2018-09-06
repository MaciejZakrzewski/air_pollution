package service

import com.softwaremill.sttp.{Id, _}
import model.{City, Commune, Station}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import props.{PropNames, PropUrls}
import scalacache._
import scalacache.redis._
import scalacache.serialization.binary._
import _root_.redis.clients.jedis._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scalacache.modes.try_._

object StationService {
  private val LOG: Logger = Logger(LoggerFactory.getLogger(StationService.getClass))

  val jedisPool = new JedisPool("localhost", 6379)
  implicit val stationsCache: Cache[String] = RedisCache(jedisPool)

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

  def getAllStationsFromApi: Option[Seq[Station]] = {
    val request = sttp.get(uri"${PropUrls.PROP_GET_ALL_STATIONS_URL}")

    implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

    LOG.debug("Sending request: {}", PropUrls.PROP_GET_ALL_STATIONS_URL)
    val response = request.send()

    cacheStations(response.unsafeBody)

    val cachedStations = getCachedStations(PropNames.PROP_STATIONS_KEY)

    val json: JsValue = Json.parse(cachedStations.getOrElse(return None))

    json.validate[Seq[Station]] match {
      case s: JsSuccess[Seq[Station]] =>
        val stations: Seq[Station] = s.get

        LOG.debug("Returning cached stations")

        Some(stations)
      case _: JsError =>
        LOG.debug("Parsing error")

        None
    }
  }

  private[this] def cacheStations(stationsString: String): Unit = {
    LOG.debug("Caching stations")

    put(PropNames.PROP_STATIONS_KEY)(stationsString)
  }

  def getCachedStations(key: String): Option[String] = {
    LOG.debug("Getting cached stations with key: {}", key)

    get(key).getOrElse(None)
  }
}

