package com.yiuzg.flo.nem12.ingest.configuration;

import ch.qos.logback.classic.spi.ConfiguratorRank;
import com.yiuzg.flo.nem12.ingest.tasklet.InitialiseIngestTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@ConfiguratorRank
public class IngestInitStepConfiguration
{
    @JobScope
    @Bean("ingestInitStep")
    public Step ingestInitStep(
            @Value("${flo.nem12.ingest.init.step.name}") String stepName,
            JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
            @Qualifier("ingestInitTasklet") Tasklet tasklet) {
        return new StepBuilder(stepName, jobRepository)
                .tasklet(tasklet, platformTransactionManager)
                .build();
    }

    @StepScope
    @Bean("ingestInitTasklet")
    public Tasklet ingestInitTasklet() {
        return new InitialiseIngestTasklet();
    }
}
