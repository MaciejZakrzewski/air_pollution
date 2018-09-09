import org.scalatest.FunSuite
import service.AirQualityIndexesService

class AirQualityIndexesServiceTest extends FunSuite {
  test("AirQualityIndexesService.getAirQualityIndex") {
    val indexes = AirQualityIndexesService.getAirQualityIndex(114)

    assert(indexes.isDefined)

    assert(indexes.nonEmpty)
  }

}
