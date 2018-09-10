package service

import com.typesafe.scalalogging.Logger
import model._
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json._
import props.{PropNames, PropUrls}

import scala.collection.mutable.ListBuffer

object SensorsService {
  private val LOG: Logger = Logger(LoggerFactory.getLogger(SensorsService.getClass))

  def getAllSensorsFromApiWithId(id: Int): Option[Seq[Sensor]] = {
    LOG.debug("Getting sensors with id: {}", id)

    val url = PropUrls.PROP_GET_ALL_SENSORS_URL + id

    parseSensorJson(ApiSenderService.getDataFromApi(url))
  }

  def getSensorData(sensorId: Int): Option[SensorData] = {
    LOG.debug("Getting sensor data with id: {}", sensorId)

    val url = PropUrls.PROP_GET_SENSOR_DATA_URL + sensorId

    parseSensorDataJson(ApiSenderService.getDataFromApi(url))
  }

  def getSensorDataForStation(stationId: Int): (Station, Seq[SensorData]) = {
    val sensors = getAllSensorsFromApiWithId(stationId)

    val sensorsData = new ListBuffer[SensorData]()

    if (sensors.isDefined)
      sensors.get.foreach(x => sensorsData += getSensorData(x.id).get)

    val filteredSensorsData = new ListBuffer[SensorData]()

    sensorsData.foreach(sensorData => {
      if (sensorData.values.isEmpty) {

      } else {
        var sensorHead = sensorData.values.head

        if (sensorHead.value.isEmpty) {
          val withoutFirst = sensorData.values.tail

          sensorHead = withoutFirst.head
        }

        val filteredValues = sensorData.values.filter(_ == sensorHead)

        val dataToPut = SensorData(sensorData.key, filteredValues)

        filteredSensorsData += dataToPut
      }
    })

    (StationsService.getStationById(stationId), filteredSensorsData.toSeq)
  }

  private[this] def parseSensorDataJson(sensorDataJson: String): Option[SensorData] = {
    implicit val sensorDataValueReads: Reads[SensorDataValue] = (
      (JsPath \ PropNames.PROP_SENSOR_DATA_VALUE_DATE).read[String] and
        (JsPath \ PropNames.PROP_SENSOR_DATA_VALUE_VALUE).readNullable[Double]
      ) (SensorDataValue.apply _)

    implicit val sensorDataReads: Reads[SensorData] = (
      (JsPath \ PropNames.PROP_SENSOR_DATA_KEY).read[String] and
        (JsPath \ PropNames.PROP_SENSOR_DATA_VALUES).read[List[SensorDataValue]]
      ) (SensorData.apply _)

    val json: JsValue = Json.parse(sensorDataJson)

    json.validate[SensorData] match {
      case s: JsSuccess[SensorData] =>
        val sensorData: SensorData = s.get

        LOG.debug("Returning sensor data")

        Some(sensorData)

      case _: JsError =>
        LOG.debug("Parsing error")

        None
    }
  }

  private[this] def parseSensorJson(sensorJson: String): Option[Seq[Sensor]] = {
    implicit val sensorParamReads: Reads[SensorParam] = (
      (JsPath \ PropNames.PROP_SENSOR_PARAM_NAME).read[String] and
        (JsPath \ PropNames.PROP_SENSOR_PARAM_FORMULA).read[String] and
        (JsPath \ PropNames.PROP_SENSOR_PARAM_CODE).read[String] and
        (JsPath \ PropNames.PROP_SENSOR_PARAM_ID).read[Int]
      ) (SensorParam.apply _)

    implicit val sensorReads: Reads[Sensor] = (
      (JsPath \ PropNames.PROP_ID).read[Int] and
        (JsPath \ PropNames.PROP_STATION_ID).read[Int] and
        (JsPath \ PropNames.PROP_SENSOR_PARAM).read[SensorParam]
      ) (Sensor.apply _)

    val json: JsValue = Json.parse(sensorJson)

    json.validate[Seq[Sensor]] match {
      case s: JsSuccess[Seq[Sensor]] =>
        val sensors: Seq[Sensor] = s.get

        LOG.debug("Returning sensors")

        Some(sensors)

      case _: JsError =>
        LOG.debug("Parsing error")

        None
    }
  }

}
