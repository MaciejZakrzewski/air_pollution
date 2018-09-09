package model

case class AirQualityIndex(stationId: Int, stIndexLevel: Option[IndexLevel], so2IndexLevel: Option[IndexLevel],
                           no2IndexLevel: Option[IndexLevel], coIndexLevel: Option[IndexLevel], pm10IndexLevel: Option[IndexLevel],
                           pm25IndexLevel: Option[IndexLevel], o3IndexLevel: Option[IndexLevel], c6h6IndexLevel: Option[IndexLevel])
