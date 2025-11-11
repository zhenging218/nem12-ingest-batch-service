package com.yiuzg.flo.nem12.ingest.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IngestJobConfiguration
{
    @Bean("ingestJob")
    public Job ingestJob(
            @Value("${flo.nem12.ingest.job.name}") String jobName, JobRepository jobRepository,
            @Qualifier("ingestInitStep") Step ingestInitStep,
            @Qualifier("ingestStep") Step ingestStep
    ) {
        return new JobBuilder(jobName, jobRepository)
                .start(ingestInitStep)
                .next(ingestStep)
                .build();
    }
}
