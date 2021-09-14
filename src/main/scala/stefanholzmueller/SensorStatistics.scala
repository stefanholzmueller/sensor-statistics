package stefanholzmueller


import cats.effect._
import fs2.{Stream, text}
import fs2.io.file.{Files, Path}


object SensorStatistics extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for
      directoryPath <- extractDirectoryPath(args)
      _ <- dummy(directoryPath).compile.drain
    yield ExitCode.Success

  def extractDirectoryPath(args: List[String]): IO[Path] =
    for
      _ <- IO.raiseWhen(args.length < 1)(new IllegalArgumentException("Usage: call with the path to the reports directory as the first argument"))
      directoryPath = Path(args(0))
      directoryExists <- Files[IO].isReadable(directoryPath)
      _ <- IO.raiseUnless(directoryExists)(new IllegalArgumentException("Error: given directory does not exist or is not readable"))
    yield directoryPath

  def dummy(path: Path): Stream[IO, Unit] =
    Files[IO].readAll(Path("test-files/leader-1.csv"))
      .through(text.utf8.decode)
      .through(text.lines)
      .drop(1)
      .intersperse("\n")
      .through(fs2.io.stdoutLines())

}