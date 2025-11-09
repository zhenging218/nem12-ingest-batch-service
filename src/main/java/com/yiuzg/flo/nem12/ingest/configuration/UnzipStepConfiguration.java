package com.yiuzg.flo.nem12.ingest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import com.yiuzg.flo.nem12.ingest.service.FileStagingService;
import com.yiuzg.flo.nem12.ingest.tasklet.UnzipTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class UnzipStepConfiguration
{
    @Bean("unzipStep")
    public Step unzipStep(@Value("${flo.nem12.ingest.unzip.step.name}") String stepName, JobRepository jobRepository,
                          PlatformTransactionManager platformTransactionManager,
                          @Qualifier("unzipTasklet") Tasklet tasklet) {
        return new StepBuilder(stepName, jobRepository)
                .tasklet(tasklet, platformTransactionManager)
                .build();
    }

    @StepScope
    @Bean("unzipTasklet")
    public Tasklet unzipTasklet(
            ObjectMapper objectMapper,
            FileArchiveService fileArchiveService,
            FileStagingService fileStagingService) {
        return new UnzipTasklet(objectMapper, fileArchiveService, fileStagingService);
    }
}
