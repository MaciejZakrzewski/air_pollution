package util

import props.PropStandards

object PercentageConverter {
  def toPercentageValue(value: Double, standardName: String): Int = {
    val standard: Double = standardName match {
      case "PM10" => PropStandards.PM10
      case "C6H6" => PropStandards.C6H6
      case "CO" => PropStandards.CO
      case "NO2" => PropStandards.NO2
      case "PM2.5" => PropStandards.PM25
      case "SO2" => PropStandards.SO2
      case "O3" => PropStandards.O3
    }

    (value / standard * 100).ceil.intValue()
  }
}