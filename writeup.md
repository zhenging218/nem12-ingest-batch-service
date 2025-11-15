# Assessment Writeup

The following content is specific to the Flo Energy Tech Assessment 2025:

## 1. What is the rationale for the technologies you have decided to use?

I have decided to use **Spring Batch** and **Spring Integration** for this assessment. This is because I have interpreted the requirements to be that of the implementation of a **Batch Job**.

The reason to implement a batch job is to make use of batch job elements to parse large amounts of data in chunks and to handle failures duing the process. The main 2 points that I wanted to address using the batch job implementation are as such:

1. The job must be able to process the file in a way that will **not overwhelm the system** by loading the entirety of the contents into memory.
2. The job must be able to **roll back erroneous input** whether it be to treat the entire ingested file as invalid or to roll back the erroneous chunk.

Particularly, I also wanted to make use of the features of Spring Batch that allows me to cover the above-mentioned issues without needing to write too much boilerplate code. The provided Tasklet and Reader/Processor/Writer features of Spring Batch allow me to implement batch jobs quickly in this case.

As for Spring Integration, it is also chosen due to its ease of use for **polling a staging folder for data to be ingested**. The provided DSL-based configuration allowed me to also implement a directory poller and job runner without writing too much boilerplate.

## 2. What would you have done differently if you had more time?

Given more time, I will integrate a **staging table** into the application to improve the robustness of the batch job. Particularly, the staging table will be used to separate the ingest process into sections (staging the file, staging a valid record and committing valid records). 

A staging table with a **process status tracking mechanism** will allow the application to **run each section independently of each other**, provide better granularity for a **retry mechanism** based on the status of the staged data as well as **better monitoring capabilities** by sectioning the data ingested into smaller records and **tracking their processing results**.

With the independent running of each section, I will also move the sections of the batch job sans the first staging ingest part to be triggered using **scheduled crons** instead.

## 3. What is the rationale for the design choices you have made?

### 1. Polymorphic record DTOs
Although not required, the NEM12 specification documents multiple record types to be handled appropriately. Although the assignment only requires the type 300 record to be inserted, it is assumed that other record types either control the record reading process and/or should be inserted into sibling tables (when they exist). The reader/processor/writer mechanism of Spring Batch also can then make use of the base type of the record DTO to implement a polymorphic processor/writer.

### 2. Validation in reader
Validation is intentionally done on the reader instead of the writer part of the batch job, so that the record fails fast.

Spring Batch also allows the reader to be implemented to map flat file records into DTOs easily, so it is a waste not to make use of such a convenient feature.

### 3. No processor implementation
As the data file contains lines that correspond to different record types, it is assumed that they should be committed to their respective data tables when those are provided. This means that the table is chosen at the writing stage based on the record's type.

As there is no other processing to be done aside from choosing the table to insert to, the processor is omitted. The preprocessing of the record data before insertion (e.g. computing consumption by adding interval values) is also only done for the data that actually goes into the table, so it was intentionally left until when the entity is created to be saved.

### 4. 2-tier file system integration configuration
As observed, the Spring Integration configuration splits the ingest and the archive into two parts.This is intentional as the current implementation processes the file while it is still in the staging folder. Therefore, the ingested file should only be archived when the job is complete.

### 5. No Spring Integration configuration to only read the same file once
This is intentional in order to support re-staging of files. This use case is to support instances where a different version of the same file is staged. This is specifically for ease of testing the assignment. In production, a staging table would be expected to be used that treats files of the same name as separate ingests.

### 6. Unit testing the batch job with chunk size 1
This is because the unit test specifically asserts based on insertion count (not the best, but a *very accurate* alternative takes time to prepare the asserts to tally the actual data inserted). In actual practice, if a record in a chunk encounters failure, all records within that chunk is rolled back by Spring Batch.

### 7. Using H2 database
This is just for ease of running the app. The DDL provided seems to be for PostgreSQL, so the drivers are included. Configuring the properties to connect to PostgreSQL shall be left to the discretion of the assessor.

### 8. Non-reactive endpoints
The batch job implementation is simple and sequential, so no WebFlux is used to over-complicate the endpoints.