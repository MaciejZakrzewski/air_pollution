package controller

import javafx.event.ActionEvent
import model.Station
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.stage.Stage
import service.StationsSaveService

class AddStationController(stations: Seq[Station], dialog: Stage) {

  private val headerLabel = new Label {
    text = "Wybierz stację"
    styleClass += "header-label"
    layoutX = 150
    layoutY = 25
  }

  private val separator = new Separator {
    layoutY = 325
    prefWidth = 600
  }

  private val addButton = new Button {
    text = "Dodaj"
    prefWidth = 150
    prefHeight = 50
    layoutX = 425
    layoutY = 340
    styleClass += "btn"
    onAction = (event: ActionEvent) => addBtnOnClick()
    disable = true
  }

  var stationsObservableBuffer: ObservableBuffer[Station] = ObservableBuffer(stations)

  private val tableView: TableView[Station] = new TableView[Station](stationsObservableBuffer) {
    layoutY = 100
    prefWidth = 600
    prefHeight = 225
  }

  private val stationNameColumn: TableColumn[Station, String] = new TableColumn[Station, String]("Nazwa stacji") {
    prefWidth = 249
    resizable = false
    editable = false
  }

  private val stationAddressColumn: TableColumn[Station, String] = new TableColumn[Station, String]("Adres") {
    prefWidth = 349
    resizable = false
    editable = false
  }

  def addBtnOnClick(): Unit = {
    StationsSaveService.addStationToCache(tableView.getSelectionModel.getSelectedItem.id)
    dialog.close()
  }

  def addStationScene(): Scene = {
    tableView.columns ++= List(stationNameColumn, stationAddressColumn)
    stationNameColumn.cellValueFactory = station => StringProperty(station.value.stationName)
    stationAddressColumn.cellValueFactory = station => StringProperty(station.value.city.name.concat(" ").concat(station.value.addressStreet.getOrElse("")))

    tableView.getSelectionModel.selectedItemProperty().addListener(e => {
      addButton.disable = false
    })

    new Scene(590, 400) {
      stylesheets = List(getClass.getResource("modal.css").toExternalForm)
      fill = Color.web("#f2f2ed")
      content = new Pane {
        prefWidth = 600
        prefHeight = 100
        styleClass += "modal-header"
        children.add(headerLabel)
        children.add(separator)
        children.add(addButton)
        children.add(tableView)
      }
    }
  }
}