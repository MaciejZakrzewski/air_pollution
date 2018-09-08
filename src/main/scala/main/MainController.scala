package main

import service.{SensorsService, StationsService}

object MainController extends App {

  StationsService.getAllStationsFromCache.get.foreach(println)

  StationsService.getStationsByName("Wrocław - Bartnicza").foreach(println)

  SensorsService.getAllSensorsFromApiWithId(114).get.foreach(println)

  println(SensorsService.getSensorData(642).get)
}