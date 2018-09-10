package controller

import javafx.event.ActionEvent
import model.{SensorData, Station}
import props.PropStandards
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{GridPane, Pane}
import scalafx.scene.paint.Color
import scalafx.scene.{Node, Scene}
import scalafx.stage.{Modality, Stage}
import service.{SensorsService, StationsSaveService, StationsService}
import util.PercentageConverter

import scala.collection.mutable.ListBuffer

class HomeController(stations: Seq[Station], val parentStage: Stage) {

  private val label = new Label {
    layoutX = 450
    layoutY = 20
    text = "SmogPOLex"
    alignmentInParent = Pos.Center
    styleClass += "smog-label"
  }

  private val addStationButton = new Button {
    text = "Dodaj stację"
    prefWidth = 200
    prefHeight = 50
    layoutX = 900
    layoutY = 20
    styleClass += "btn" += "add-station-btn"
    onAction = (event: ActionEvent) => addStationOnClick()
  }

  private val refreshButton = new Button {
    text = "Odśwież"
    prefWidth = 200
    prefHeight = 50
    layoutX = 75
    layoutY = 20
    styleClass += "btn" += "refresh-btn"
    onAction = (event: ActionEvent) => refresh()
  }

  private val separator = new Separator {
    layoutY = 90
    prefWidth = 1200
  }

  def createStationLabel(station: Station): Label = {
    new Label {
      text = station.stationName.concat(" ").concat(station.addressStreet.getOrElse(""))
      padding = Insets(5, 0, 0, 0)
      styleClass += "station-label"
    }
  }

  def createPane(childrenList: List[Node]): Pane = {
    new Pane {
      prefWidth = 1075
      layoutX = 50
      layoutY = 75
      styleClass += "pane"
      children = childrenList
      padding = Insets(0, 0, 5, 0)
    }
  }

  def createLabel(txt: String, i: Int, x: Double): Label = {
    new Label {
      layoutY = 25 * i + 5
      layoutX = x
      text = txt
    }
  }

  def createProgressBar(value: Double, i: Int): ProgressBar = new ProgressBar {
    layoutY = 25 * i + 5
    layoutX = 50
    prefWidth = 290
    var vale: Double = value / 100
    progress = vale
    var color: String = vale match {
      case x if x < 0.25 => "green"
      case x if x >= 0.25 && x < 0.50 => "yellow"
      case x if x >= 0.50 && x < 0.75 => "orange"
      case x if x >= 0.75 => "red"
    }
    style = s"-fx-accent: $color"
  }

  def prepareData(stations: Seq[Station]): Map[Station, Seq[SensorData]] = {
    var map: Map[Station, Seq[SensorData]] = Map[Station, Seq[SensorData]]()
    stations.foreach(station => map += (station -> SensorsService.getSensorDataForStation(station.id)._2))
    map
  }

  def createAirQualityIcon(stationId: Int, i: Int, width: Int): Option[ImageView] = {
    service.AirQualityIndexesService.getAirQualityIndex(stationId) match {
      case Some(value) =>
        value.stIndexLevel match {
          case Some(indexLevel) =>
            val img: Image = new Image(s"controller/img/${indexLevel.id}.png")
            Some(new ImageView(img))
          case None => None
        }
      case None => None
    }
  }

  def createRemoveStationBtn(stationId: Int): Button = new Button {
    text = "X"
    prefWidth = 15
    prefHeight = 15
    layoutX = 1050
    layoutY = -35
    styleClass += "remove-btn"
    onAction = (event: ActionEvent) => removeStation(stationId)
  }

  private def createGridPane(map: Map[Station, Seq[SensorData]]): GridPane = new GridPane() {
    hgap = 10
    vgap = 10
    padding = Insets(50)

    var x = 0

    map.foreach({ case (station, sensorDataSeq) =>
      add(createStationLabel(station), 0, x)
      x += 1

      val childrenList = new ListBuffer[Node]()
      childrenList += createRemoveStationBtn(station.id)

      var c = 0
      for ((sensorData, j) <- sensorDataSeq.view.zipWithIndex) {
        sensorData.values.head.value match {
          case Some(value) =>
            childrenList += createLabel(sensorData.key, c, 10)
            childrenList += createProgressBar(PercentageConverter.toPercentageValue(value, sensorData.key), c)
            childrenList += createLabel(PercentageConverter.toPercentageValue(value, sensorData.key).toString.concat("%"), c, 345)
            childrenList += createLabel(value.toString.concat(PropStandards.UNIT), c, 380)
            c += 1
          case None =>
        }
      }
      createAirQualityIcon(station.id, c, 600) match {
        case Some(img) =>
          img.setX(600d)
          img.setY(5d)
          childrenList += img
        case None =>
      }

      add(createPane(childrenList.toList), 0, x)
      x += 1
    }
    )
  }

  private val scrollPane: ScrollPane = new ScrollPane() {
    hbarPolicy = ScrollBarPolicy.AsNeeded
    layoutY = 90
    prefHeight = 520
    prefWidth = 1210
    content = createGridPane(prepareData(stations))
  }

  def homeScene(): Scene = {
    new Scene(1200, 600) {
      stylesheets = List(getClass.getResource("style.css").toExternalForm)
      fill = Color.web("#f2f2ed")
      getChildren.addAll(addStationButton, refreshButton, label, separator, scrollPane)
    }
  }

  def addStationOnClick(): Unit = {
    val dialog: Stage = new Stage() {
      resizable = false
    }
    dialog.initOwner(parentStage)
    dialog.scene = new AddStationController(StationsService.getAllStationsFromCache.getOrElse(Seq.empty), dialog).addStationScene()
    dialog.initModality(Modality.ApplicationModal)
    dialog.showAndWait()
    refresh()
  }

  def refresh(): Unit = {
    scrollPane.content = createGridPane(prepareData(StationsSaveService.getCachedStationsObjects))
  }

  def removeStation(stationId: Int): Unit = {
    StationsSaveService.removeStationsFromCache(List(stationId))
    refresh()
  }
}