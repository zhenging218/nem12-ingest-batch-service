package com.yiuzg.flo.nem12.ingest.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.service.Nem12IngestService;
import com.yiuzg.flo.nem12.ingest.utilities.BatchJobUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class BatchJobNem12IngestServiceImpl implements Nem12IngestService
{
    private final ObjectMapper objectMapper;
    private final JobLauncher jobLauncher;
    private final Job ingestJob;

    @Autowired
    public BatchJobNem12IngestServiceImpl(
            ObjectMapper objectMapper, JobLauncher jobLauncher,
            @Qualifier("ingestJob") Job ingestJob)
    {
        this.objectMapper = objectMapper;
        this.jobLauncher = jobLauncher;
        this.ingestJob = ingestJob;
    }

    @Override
    public Mono<String> ingest(FileDto fileIngest)
    {
        return Mono.fromCallable(() -> {
            try
            {
                var execution = jobLauncher.run(ingestJob, new JobParametersBuilder()
                        .addString(BatchJobConstants.JOB_PARAM_INGEST_FILE_PATH_KEY,
                                BatchJobUtil.createJobParameterString(objectMapper, fileIngest))
                        .addLocalDateTime(BatchJobConstants.JOB_PARAM_TIMESTAMP_KEY, LocalDateTime.now())
                        .toJobParameters());

                return Long.toString(execution.getJobId());
            }
            catch(JobExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
