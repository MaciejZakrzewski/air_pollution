name := "air_pollution"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.102-R11",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.3"
)

libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.3.2"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.10"

// https://mvnrepository.com/artifact/com.github.cb372/scalacache-redis
libraryDependencies += "com.github.cb372" %% "scalacache-redis" % "0.24.3"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
