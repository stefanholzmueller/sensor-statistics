name := "sensor-statistics"
version := "0.1"

scalaVersion := "3.0.2"
scalacOptions := Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-core" % "3.1.1",
  "co.fs2" %% "fs2-io" % "3.1.1"
)