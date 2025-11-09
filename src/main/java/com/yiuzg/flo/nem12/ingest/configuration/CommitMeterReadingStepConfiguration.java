package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.constants.EStagingState;
import com.yiuzg.flo.nem12.ingest.constants.Nem12Constants;
import com.yiuzg.flo.nem12.ingest.entity.impl.Nem12StagingEntity;
import com.yiuzg.flo.nem12.ingest.processor.MeterReadingCommitItemProcessor;
import com.yiuzg.flo.nem12.ingest.query.Nem12StagingQueryProvider;
import com.yiuzg.flo.nem12.ingest.repository.MeterReadingRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaQueryProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Configuration
public class CommitMeterReadingStepConfiguration
{
    @JobScope
    @Bean("commitMeterReadingStep")
    public Step commitStep(
            JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
            @Value("${flo.nem12.meter-reading.commit.step.name}") String stepName,
            @Value("${flo.nem12.meter-reading.commit.chunk-size}") int chunkSize,
            @Qualifier("meterReadingStagingReader") ItemReader<Nem12StagingEntity> itemReader,
            @Qualifier("meterReadingStagingProcessor") ItemProcessor<Nem12StagingEntity, Nem12StagingEntity> itemProcessor,
            @Qualifier("stagingItemWriter") ItemWriter<Nem12StagingEntity> itemWriter
            ) {
        return new StepBuilder(stepName, jobRepository)
                .<Nem12StagingEntity, Nem12StagingEntity>chunk(chunkSize, platformTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @StepScope
    @Bean("meterReadingStagingProcessor")
    public ItemProcessor<Nem12StagingEntity, Nem12StagingEntity> meterReadingStagingProcessor(
            ObjectMapper objectMapper,
            MeterReadingRepository repository
    ) {
        return new MeterReadingCommitItemProcessor(objectMapper, repository);
    }

    @StepScope
    @Bean("meterReadingStagingQueryProvider")
    public JpaQueryProvider meterReadingStagingQueryProvider() {
        return new Nem12StagingQueryProvider(
                Nem12Constants.NEM12_INTERVAL_DATA_IND,
                "key", EStagingState.NEW, EStagingState.FAILED);
    }

    @StepScope
    @Bean("meterReadingStagingReader")
    public ItemReader<Nem12StagingEntity> meterReadingStagingReader(
            EntityManagerFactory entityManagerFactory,
            @Qualifier("meterReadingStagingQueryProvider") JpaQueryProvider queryProvider,
            @Value("${flo.nem12.meter-reading.commit.chunk-size}") int pageSize
    ) {
        return new JpaCursorItemReaderBuilder<Nem12StagingEntity>()
                .entityManagerFactory(entityManagerFactory)
                .queryProvider(queryProvider)
                .hintValues(Map.of(
                        "hibernate.jdbc.fetch_size", pageSize,
                        "org.hibernate.readOnly", true))
                .build();
    }
}
