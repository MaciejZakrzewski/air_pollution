package main

import service.StationService

object MainController extends App {
  StationService.getAllStationsFromApi.get.foreach(println)

}