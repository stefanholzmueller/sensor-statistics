package stefanholzmueller

import cats.Id
import fs2.Stream
import stefanholzmueller.SensorStatistics.accumulateStatistics

class SensorStatisticsSuite extends munit.FunSuite {

  private val s1: SensorId = SensorId("s1")
  private val s2: SensorId = SensorId("s2")
  private val s3: SensorId = SensorId("s3")

  test("integration test") {
    val measurements: Stream[Id, SensorMeasurement] = Stream(
      SensorMeasurement(s1, Some(10)),
      SensorMeasurement(s2, Some(88)),
      SensorMeasurement(s1, None),
      SensorMeasurement(s2, Some(80)),
      SensorMeasurement(s3, None),
      SensorMeasurement(s2, Some(78)),
      SensorMeasurement(s1, Some(98)),
    )
    val accumulatorStream = measurements.fold[Accumulator](Map.empty)(SensorStatistics.accumulateStatistics)
    val sensorDataStream = accumulatorStream.map(accumulator => SensorStatistics.printStatistics(2, accumulator))
    val sensorDataString = sensorDataStream.compile.string

    assertEquals(
      sensorDataString.replaceAll("\r\n", "\n"),
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
        |""".stripMargin.replaceAll("\r\n", "\n")
    )
  }

}
