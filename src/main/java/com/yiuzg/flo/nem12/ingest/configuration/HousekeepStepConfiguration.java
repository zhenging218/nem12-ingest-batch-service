package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HousekeepStepConfiguration
{
    @JobScope
    @Bean("housekeepStep")
    public Step housekeepStep(
            @Value("${flo.nem12.housekeep.step.name}") String stepName,
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("housekeepTasklet") Tasklet tasklet) {
        return new StepBuilder(stepName, jobRepository)
                .tasklet(tasklet, platformTransactionManager)
                .build();
    }

    @StepScope
    @Bean("housekeepTasklet")
    public Tasklet housekeepTasklet(@Value("#{jobParameters[" + BatchJobConstants.JOB_PARAM_INGEST_FILE_KEY + "]}") String ingestFile) {
        return (contribution, chunkContext) -> {
            FileUtils.delete(new FileSystemResource(ingestFile).getFile());
            return RepeatStatus.FINISHED;
        };
    }
}
