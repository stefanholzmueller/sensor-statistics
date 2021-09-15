package stefanholzmueller

class AccumulateStatisticsSuite extends munit.FunSuite {

  private val s1: SensorId = SensorId("s1")
  private val s2: SensorId = SensorId("s2")

  test("initial measurement") {
    assertEquals(
      SensorStatistics.accumulateStatistics(Map.empty, SensorMeasurement(s1, Some(10))),
      Map(s1 -> AccumulatedSensorData(0, Some(AccumulatedStatistics(1, 10, 10, 10))))
    )
  }

  test("initial NaN measurement") {
    assertEquals(
      SensorStatistics.accumulateStatistics(Map.empty, SensorMeasurement(s2, None)),
      Map(s2 -> AccumulatedSensorData(1, None))
    )
  }

  test("accumulate invalid and invalid of same sensor") {
    assertEquals(
      SensorStatistics.accumulateStatistics(
        Map(s2 -> AccumulatedSensorData(1, None)),
        SensorMeasurement(s2, None)
      ),
      Map(s2 -> AccumulatedSensorData(2, None))
    )
  }

  test("accumulate invalid and valid of same sensor") {
    assertEquals(
      SensorStatistics.accumulateStatistics(
        Map(s2 -> AccumulatedSensorData(1, None)),
        SensorMeasurement(s2, Some(123))
      ),
      Map(s2 -> AccumulatedSensorData(1, Some(AccumulatedStatistics(1, 123, 123, 123))))
    )
  }

  test("accumulate valid and invalid of same sensor") {
    assertEquals(
      SensorStatistics.accumulateStatistics(
        Map(s2 -> AccumulatedSensorData(0, Some(AccumulatedStatistics(5, 100, 20, 20)))),
        SensorMeasurement(s2, Some(123))
      ),
      Map(s2 -> AccumulatedSensorData(0, Some(AccumulatedStatistics(6, 223, 20, 123))))
    )
  }

}
