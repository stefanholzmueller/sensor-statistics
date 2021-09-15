package stefanholzmueller


import cats.effect.*
import fs2.{Stream, text}
import fs2.io.file.{Files, Path}


object SensorStatistics extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for
      directoryPath <- extractDirectoryPath(args)
      files = Files[IO].walk(directoryPath).filter(_.extName == ".csv")
      fileCount <- files.compile.count
      measurements = files.flatMap(readSensorMeasurements)
      accumulator = measurements.fold[Accumulator](Map.empty)(accumulateStatistics)
      _ <- accumulator.map(printStatistics(fileCount, _)).through(fs2.io.stdoutLines()).compile.drain
    yield ExitCode.Success

  def extractDirectoryPath(args: List[String]): IO[Path] =
    for
      _ <- IO.raiseWhen(args.length < 1)(new IllegalArgumentException("Usage: call with the path to the reports directory as the first argument"))
      directoryPath = Path(args(0))
      directoryExists <- Files[IO].isReadable(directoryPath)
      _ <- IO.raiseUnless(directoryExists)(new IllegalArgumentException("Error: given directory does not exist or is not readable"))
    yield directoryPath

  def readSensorMeasurements(filePath: Path): Stream[IO, SensorMeasurement] =
    Files[IO].readAll(filePath)
      .through(text.utf8.decode)
      .through(text.lines)
      .filter(_.nonEmpty)
      .drop(1) // skip the header line
      .map(parseSensorMeasurement)

  def parseSensorMeasurement(row: String): SensorMeasurement =
    val parts = row.split(',')
    val id = SensorId(parts(0))
    val humidity = parts(1).toByteOption
    SensorMeasurement(id, humidity)

  def accumulateStatistics(acc: Accumulator, measurement: SensorMeasurement): Accumulator =
    acc.updatedWith(measurement.id) {
      case None =>
        Some(initialData(measurement.humidity))
      case Some(acc) =>
        Some(accumulateData(measurement.humidity, acc))
    }

  private def initialData(humidity: Option[Byte]): AccumulatedSensorData =
    humidity.fold(
      AccumulatedSensorData(failed = 1, data = None)
    )(
      humidity => AccumulatedSensorData(failed = 0, data = Some(AccumulatedStatistics(1, humidity, humidity, humidity)))
    )

  private def accumulateData(humidity: Option[Byte], acc: AccumulatedSensorData): AccumulatedSensorData =
    (humidity, acc) match
      case (None, _) =>
        acc.copy(failed = acc.failed + 1)
      case (Some(h), AccumulatedSensorData(_, None)) =>
        acc.copy(data = Some(AccumulatedStatistics(1, h, h, h)))
      case (Some(h), AccumulatedSensorData(_, Some(data))) =>
        acc.copy(data = Some(AccumulatedStatistics(
          count = data.count + 1,
          sum = data.sum + h,
          min = if h > data.min then data.min else h,
          max = if h < data.max then data.max else h
        )))

  def printStatistics(fileCount: Long, accumulator: Accumulator): String =
    val numSuccessful = accumulator.values.map(_.data.map(_.count).getOrElse(0)).sum
    val numFailed = accumulator.values.map(_.failed).sum
    val numProcessed = numSuccessful + numFailed
    val sortedSensorData = sortSensorData(accumulator)
    val sensorDataString = sortedSensorData.map(sensorData =>
      s"${sensorData.id},${sensorData.stats.map(stats => s"${stats.min},${stats.avg.toLong},${stats.max}").getOrElse("NaN,NaN,NaN")}"
    ).mkString("\n")
    s"""Num of processed files: $fileCount
       |Num of processed measurements: $numProcessed
       |Num of failed measurements: $numFailed
       |
       |Sensors with highest avg humidity:
       |
       |sensor-id,min,avg,max
       |$sensorDataString
       |""".stripMargin

  def sortSensorData(accumulator: Accumulator): Seq[SensorData] =
    val sensorDataList = accumulator.toList.map((id, stats) => SensorData(
      id = id,
      stats = stats.data.map(data => Statistics(
        min = data.min,
        avg = data.sum.toDouble / data.count,
        max = data.max
      ))
    ))
    sensorDataList.sortBy(_.stats.map(_.avg))(Ordering[Option[Double]].reverse)

}


opaque type SensorId = String
object SensorId:
  def apply(str: String): SensorId = str

case class SensorMeasurement(id: SensorId, humidity: Option[Byte])

case class AccumulatedStatistics(count: Int, sum: Long, min: Byte, max: Byte)

case class AccumulatedSensorData(failed: Long, data: Option[AccumulatedStatistics])

type Accumulator = Map[SensorId, AccumulatedSensorData]

case class Statistics(min: Byte, avg: Double, max: Byte)

case class SensorData(id: SensorId, stats: Option[Statistics])
