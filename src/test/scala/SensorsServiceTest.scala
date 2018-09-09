import org.scalatest.FunSuite
import service.SensorsService

class SensorsServiceTest extends FunSuite {
  test("SensorsService.getAllSensorsFromApiWithId") {
    val sensors = SensorsService.getAllSensorsFromApiWithId(114)

    assert(sensors.isDefined)

    assert(sensors.nonEmpty)
  }

  test("SensorsService.getSensorData") {
    val data = SensorsService.getSensorData(642)

    assert(data.isDefined)

    assert(data.nonEmpty)
  }

  test("SensorsService.getSensorDataForStations") {
    val data = SensorsService.getSensorDataForStation(950)

    assert(data._1 != null)

    assert(data._2.nonEmpty)
  }

}
