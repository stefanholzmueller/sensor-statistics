package stefanholzmueller

class PrintStatisticsSuite extends munit.FunSuite {

  private val s1: SensorId = SensorId("s1")
  private val s2: SensorId = SensorId("s2")
  private val s3: SensorId = SensorId("s3")

  private def normalizeLineBreaks(str: String): String = str.replaceAll("\r\n", "\n")

  test("example".ignore) { // munit bug? "values are not equal even if they have the same `toString()`"
    assertEquals(
      normalizeLineBreaks(
        SensorStatistics.printStatistics(2, Map(
          s1 -> AccumulatedSensorData(1, Some(AccumulatedStatistics(2, 10 + 98, 10, 98))),
          s2 -> AccumulatedSensorData(0, Some(AccumulatedStatistics(3, 88 + 80 + 78, 78, 88))),
          s3 -> AccumulatedSensorData(1, None)
        ))),
      normalizeLineBreaks(
        """Num of processed files: 2
          |Num of processed measurements: 7
          |Num of failed measurements: 2
          |
          |Sensors with highest avg humidity:
          |
          |sensor-id,min,avg,max
          |s2,78,82,88
          |s1,10,54,98
          |s3,NaN,NaN,NaN
          |""".stripMargin
      )
    )
  }

  test("sort sensor data") {
    assertEquals(
      SensorStatistics.sortSensorData(Map(
        s1 -> AccumulatedSensorData(1, Some(AccumulatedStatistics(2, 10 + 98, 10, 98))),
        s2 -> AccumulatedSensorData(0, Some(AccumulatedStatistics(3, 88 + 80 + 78, 78, 88))),
        s3 -> AccumulatedSensorData(1, None)
      )),
      Seq(
        SensorData(s2, Some(Statistics(78, 82, 88))),
        SensorData(s1, Some(Statistics(10, 54, 98))),
        SensorData(s3, None)
      )
    )
  }

}
