@startuml
participant inbound as i
boundary receiver as r
participant archive as a
participant staging as s
participant "batch job service" as bjs
database "table_meter_readings" as readings
database "table_nem12_staging" as nem12

== stage 1: inbound file receiving ==

== stage 2: unzip file contents ==

== stage 3: process files ==

== stage 4: commit meter readings ==

@enduml