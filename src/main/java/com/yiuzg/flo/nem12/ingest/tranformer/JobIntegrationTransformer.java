package com.yiuzg.flo.nem12.ingest.tranformer;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;

@Component
public class JobIntegrationTransformer
{
    private final Job ingestJob;

    @Autowired
    public JobIntegrationTransformer(@Qualifier("ingestJob") Job ingestJob)
    {
        this.ingestJob = ingestJob;
    }

    @Transformer
    public JobLaunchRequest launchJob(Message<File> message) {
        return new JobLaunchRequest(ingestJob, new JobParametersBuilder()
                .addString(BatchJobConstants.JOB_PARAM_INGEST_FILE_KEY, message.getPayload().getAbsolutePath())
                .addLocalDateTime(BatchJobConstants.JOB_PARAM_TIMESTAMP_KEY, LocalDateTime.now())
                .toJobParameters());
    }
}
