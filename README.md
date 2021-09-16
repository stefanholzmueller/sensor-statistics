## Sensor Statistics

### Run

A small set of test data is included in the directory `test-files`.


### Assumptions and limitations

* Not much error handling exists; the input files are assumed to be readable, well-formed and UTF-8 encoded.
* Parsing is implemented in a simple way and unexpected input will either cause an exception or silently ignore invalid rows.
* Sensor IDs cannot contain commas which would have to be escaped in CSV files.