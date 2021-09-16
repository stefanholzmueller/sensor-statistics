## Sensor Statistics

This command-line tool processes sensor report files and prints out statistics.

### Quick start

```
java -jar sensor-statistics.jar test-files
```

### Instructions

#### Prerequisites

The project is built with [SBT](https://www.scala-sbt.org).

#### Run via SBT

```
sbt "run path/to/directory"
```

#### Build a JAR file

`sbt assembly` will create an executable JAR file that can be used like this:
```
java -jar target/scala-3.0.1/sensor-statistics-assembly-1.0.jar reports
```
For convenience, the built and renamed JAR is included in this repository as `sensor-statistics.jar`.

#### Test files

A small set of test data is included in the directory `test-files`.

### Implementation notes

* Built with Scala 3 and fs2/cats-effect
* Files are read in a streaming fashion and can be very large
* Statistics are also accumulated without keeping the individual measurements in memory
* Only at the very end, for purposes of sorting by average humidity, the list of sensors is realized in memory

### Assumptions and limitations

* Not much error handling exists; the input files are assumed to be readable, well-formed and UTF-8 encoded.
* Parsing is implemented in a simple way and unexpected input will either cause an exception or silently ignore invalid rows.
* Sensor IDs cannot contain commas which would have to be escaped in CSV files.
