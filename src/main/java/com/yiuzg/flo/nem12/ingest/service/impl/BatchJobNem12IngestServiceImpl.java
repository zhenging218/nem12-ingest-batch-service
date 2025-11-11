package com.yiuzg.flo.nem12.ingest.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.dto.IngestResponseDto;
import com.yiuzg.flo.nem12.ingest.service.Nem12IngestService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BatchJobNem12IngestServiceImpl implements Nem12IngestService
{
    private final TaskExecutor taskExecutor;
    private final JobLauncher jobLauncher;
    private final Job ingestJob;

    @Autowired
    public BatchJobNem12IngestServiceImpl(
            TaskExecutor taskExecutor, JobLauncher jobLauncher,
            @Qualifier("ingestJob") Job ingestJob)
    {
        this.taskExecutor = taskExecutor;
        this.jobLauncher = jobLauncher;
        this.ingestJob = ingestJob;
    }

    @Override
    public IngestResponseDto ingest(FileDto fileIngest)
    {
        LocalDateTime now = LocalDateTime.now();
        taskExecutor.execute(() -> {
            try
            {
                jobLauncher.run(ingestJob, new JobParametersBuilder()
                        .addString(BatchJobConstants.JOB_PARAM_INGEST_FILE_KEY, fileIngest.getObjectKey())
                        .addLocalDateTime(BatchJobConstants.JOB_PARAM_TIMESTAMP_KEY, now)
                        .toJobParameters());
            } catch (JobExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        return new IngestResponseDto(now);
    }
}
