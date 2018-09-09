package service

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

object StationsService {
  private val LOG: Logger = Logger(LoggerFactory.getLogger(StationsService.getClass))

  val jedisPool = new JedisPool(PropUrls.PROP_LOCALHOST, PropUrls.REDIS_PORT)
  implicit val stationsCache: Cache[String] = RedisCache(jedisPool)

  def getAllStationsFromApi: Option[Seq[Station]] = {

    cacheStations(ApiSenderService.getDataFromApi(PropUrls.PROP_GET_ALL_STATIONS_URL))

    val cachedStations = getCachedStations(PropNames.PROP_STATIONS_KEY)

    parseStationJson(cachedStations.getOrElse(return None))
  }

  def getAllStationsFromCache: Option[Seq[Station]] = {
    LOG.debug("Getting stations from cache")

    val cachedStations = getCachedStations(PropNames.PROP_STATIONS_KEY)

    parseStationJson(cachedStations.getOrElse(return None))
  }

  def getCachedStations(key: String): Option[String] = {
    LOG.debug("Getting cached stations with key: {}", key)

    get(key).getOrElse(None)
  }

  def getStationsByName(name: String): Seq[Station] = {
    val stations = getAllStationsFromCache

    if (stations.isDefined)
      stations.get.filter(_.stationName == name)
    else
      Nil
  }

  def getStationById(stationId: Int): Station = {
    val stations = getAllStationsFromCache

    if (stations.isDefined) {
      val filteredStations = stations.get.filter(_.id == stationId)

      filteredStations.head
    } else {
      null
    }
  }

  def getStationsByIds(stationsIds: List[Int]): Seq[Station] = {
    val stations = getAllStationsFromCache

    stations.get.filter(x => stationsIds.contains(x.id))
  }

  private[this] def cacheStations(stationsString: String): Unit = {
    LOG.debug("Caching stations")

    put(PropNames.PROP_STATIONS_KEY)(stationsString)
  }

  private[this] def parseStationJson(stationJson: String): Option[Seq[Station]] = {
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

    val json: JsValue = Json.parse(stationJson)

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
}

