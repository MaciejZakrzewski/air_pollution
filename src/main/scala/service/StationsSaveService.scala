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
import service.StationsService.{LOG, jedisPool}

import scala.collection.mutable.ListBuffer

object StationsSaveService {
  private val LOG: Logger = Logger(LoggerFactory.getLogger(StationsSaveService.getClass))

  val jedisPool = new JedisPool(PropUrls.PROP_LOCALHOST, PropUrls.REDIS_PORT)
  implicit val stationsIdsCache: Cache[List[Int]] = RedisCache(jedisPool)


  def addStationToCache(stationId: Int): Unit = {
    LOG.debug("Adding stations id to cache: {}", stationId)

    val stationsIds: ListBuffer[Int] = getCachedStationsIds.to[ListBuffer]
    stationsIds += stationId

    put(PropNames.PROP_SAVED_STATIONS_KEY)(stationsIds.toList)
  }

  def removeStationsFromCache(stationIds: List[Int]): Unit = {
    LOG.debug("Removing stations ids from cache: {}", stationIds)

    val cachedIds = get(PropNames.PROP_SAVED_STATIONS_KEY)

    if (cachedIds.isSuccess) {
      if (cachedIds.get.isDefined) {
        val afterRemove = cachedIds.get.get.filter(x => !stationIds.contains(x))

        put(PropNames.PROP_SAVED_STATIONS_KEY)(afterRemove)
      }
    }
  }

  def clearStationsFromCache(): Unit = {
    LOG.debug("Clearing stations ids from cache")

    remove(PropNames.PROP_SAVED_STATIONS_KEY)
  }

  def getCachedStationsIds: List[Int] = {
    LOG.debug("Getting cached stations ids")

    val cachedIds = get(PropNames.PROP_SAVED_STATIONS_KEY).getOrElse(None)

    cachedIds.getOrElse(List.empty)
  }

  def getCachedStationsObjects: Seq[Station] = {
    StationsService.getStationsByIds(getCachedStationsIds)
  }
}
