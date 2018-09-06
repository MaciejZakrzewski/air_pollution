package main

import props.PropNames
import service.StationService

object MainController extends App {
//  StationService.getAllStationsFromApi.get.foreach(println)

  StationService.getAllStationsFromCache.get.foreach(println)
}