@startuml
boundary staging as i
participant "File Ingest Integration" as t
participant IngestInitJob as iij
database "Batch Job Context" as jc
participant IngestJob as ij
database meter_readings as db
boundary archive as s

== new file moved into staging folder ==

i -> t : polled file
activate t
t -> iij : polled file path
activate iij
iij -> jc : save path to context
deactivate iij

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
ij --> t
deactivate ij
== archive source file ==
t -> s : move file into archive
deactivate t

@enduml