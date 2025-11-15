# Sequence Diagram

As GitHub does not support loading PlantUML generated UML diagrams, the PlantUML script is included separately. The image of the diagram generated has been included as a PNG file in the *images* folder and linked to in the main [README](./readme.md).

@startuml
boundary staging as i
participant "File Ingest Integration" as t
database "Batch Job Context" as jc
participant "Ingest Batch Job" as ij
database meter_readings as db
boundary archive as s

== new file moved into staging folder ==

i -> t : polled file
activate t
t -> ij : polled file path
activate ij
ij -> jc : save path to context
deactivate ij

== ingest file to insert data ==

ij -> jc : get path from context
activate ij
activate jc
jc --> ij : file path
deactivate jc
loop every line in file
ij -> ij : read record
activate ij
deactivate ij
alt record is type 200
ij -> ij : set current nmi
else record is type 300
ij -> db : fetch existing record
activate db
db --> ij : fetch result
deactivate db
alt no record exist
ij -> db : save new record
end
activate db
deactivate db
else record is type 900
ij -> ij : indicate end file
end
end
ij -> jc : batch job result
deactivate ij
== archive source file ==
t -> s : move file into archive
deactivate t

@enduml