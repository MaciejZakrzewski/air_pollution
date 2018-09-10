package main

import controller.HomeController
import model.Station
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import service.{StationsSaveService, StationsService}

object MainController extends JFXApp {

  StationsService.getAllStationsFromApi

  val stations: Seq[Station] = StationsSaveService.getCachedStationsObjects
  stage = new PrimaryStage {
    resizable = false
    title = "SmogPOLex"
    scene = new HomeController(stations, stage).homeScene()
  }
}