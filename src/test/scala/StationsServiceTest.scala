import org.scalatest.FunSuite
import service.StationsService

class StationsServiceTest extends FunSuite {
  test("StationsService.getAllStationsFromApi") {
    val stations = StationsService.getAllStationsFromApi

    assert(stations.isDefined)

    assert(stations.nonEmpty)
  }

  test("StationsService.getAllStationsFromCache") {
    val stations = StationsService.getAllStationsFromCache

    assert(stations.isDefined)

    assert(stations.nonEmpty)
  }

  test("StationsService.getStationsByName") {
    val stations = StationsService.getStationsByName("Wrocław - Bartnicza")

    assert(stations.nonEmpty)

    assert(stations.size == 1)
  }

}
