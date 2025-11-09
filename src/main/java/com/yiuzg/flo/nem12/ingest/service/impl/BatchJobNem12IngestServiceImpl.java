package com.yiuzg.flo.nem12.ingest.service.impl;

import com.yiuzg.flo.nem12.ingest.dto.FileIngestDto;
import com.yiuzg.flo.nem12.ingest.service.Nem12IngestService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class BatchJobNem12IngestServiceImpl implements Nem12IngestService
{
    private final JobLauncher jobLauncher;
    private final Job ingestJob;

    @Autowired
    public BatchJobNem12IngestServiceImpl(JobLauncher jobLauncher,
          @Qualifier("ingestJob") Job ingestJob)
    {
        this.jobLauncher = jobLauncher;
        this.ingestJob = ingestJob;
    }

    @Override
    public Mono<String> ingest(FileIngestDto fileIngest)
    {
        return Mono.fromCallable(() -> {
            try
            {
                var execution = jobLauncher.run(ingestJob, new JobParametersBuilder()
                        .addString("filePath", fileIngest.getObjectKey())
                        .addLong("timestamp", Instant.now().toEpochMilli())
                        .toJobParameters()
                );

                return Long.toString(execution.getJobId());
            }
            catch(JobExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
