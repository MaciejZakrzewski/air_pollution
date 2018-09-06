package model

case class Station(id: Int, stationName: String, gegrLat: String, gegrLon: String, addressStreet: Option[String], city: City)
