package stefanholzmueller


class SensorMeasurementSuite extends munit.FunSuite {

  test("parse measurement") {
    assertEquals(SensorStatistics.parseSensorMeasurement("s1,10"), SensorMeasurement(SensorId("s1"), Some(10)))
  }

  test("parse NaN") {
    assertEquals(SensorStatistics.parseSensorMeasurement("s2,NaN"), SensorMeasurement(SensorId("s2"), None))
  }

  test("parse invalid row") {
    assertEquals(SensorStatistics.parseSensorMeasurement("s3,abc"), SensorMeasurement(SensorId("s3"), None))
  }

}
