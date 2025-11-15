# nem12-ingest-batch-service

This repository is a simple Spring Batch + Spring Integration application to ingest .csv files of NEM12 specification.

## Dependencies

The application requires Java 21 to run. It uses Spring Boot 3 and is built via Maven.

## Building the application

To build the application, run the maven goals using ```mvn clean install```. To build without running the unit tests, specify ```-DskipTests``` when running the maven goals.

## Running the application

To run the built .jar file, navigate to the build directory ```target``` and run the .jar file with the ```java -jar``` command.

The staging and archive directory must be specified. You can do this by specifying it along with the command to run the jar:

```flo.nem12.ingest.staging.file-system-local.location=<your staging directory location>```

```flo.nem12.ingest.archive.file-system-local.location=./<your archive directory location>```

if the directories are not specified, directories will be generated at ```./staging``` and ```./archive``` respectively. To turn off automatic directory genration, you can set the following properties to ```false``` by specifying it in the command to run the jar:

```flo.nem12.ingest.staging.file-system-local.location.auto-create=<true/false>```

```flo.nem12.ingest.archive.file-system-local.location.auto-create=<true/false>```