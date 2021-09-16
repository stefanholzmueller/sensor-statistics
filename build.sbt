name := "sensor-statistics"
version := "1.0"
organization := "stefanholzmueller"

scalaVersion := "3.0.1"
scalacOptions := Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-core" % "3.1.1",
  "co.fs2" %% "fs2-io" % "3.1.1",
  "org.scalameta" %% "munit" % "0.7.29" % Test
)