package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.entity.MeterReadingEntity;
import com.yiuzg.flo.nem12.ingest.mapper.Nem12LineMapper;
import com.yiuzg.flo.nem12.ingest.processor.Nem12ItemProcessor;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class IngestStepConfiguration
{
    @JobScope
    @Bean("ingestStep")
    public Step ingestStep(@Value("${flo.nem12.ingest.step.name}") String stepName,
       JobRepository jobRepository,
       PlatformTransactionManager platformTransactionManager,
       @Qualifier("nem12ItemReader") ItemReader<Nem12RecordDto> itemReader,
       @Qualifier("ingestItemProcessor") ItemProcessor<Nem12RecordDto, MeterReadingEntity> itemProcessor,
       @Qualifier("meterReadingEntityWriter") ItemWriter<MeterReadingEntity> itemWriter,
       @Value("${flo.nem12.ingest.chunk-size}") int chunkSize
    ) {
        return new StepBuilder(stepName, jobRepository)
                .<Nem12RecordDto, MeterReadingEntity>chunk(chunkSize, platformTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @StepScope
    @Bean("nem12LineTokenizer")
    public LineTokenizer nem12LineTokenizer(
            @Value("${flo.nem12.ingest.line.delimiter}") String delimiter
    ) {
        return new DelimitedLineTokenizer(delimiter);
    }

    @StepScope
    @Bean("nem12LineMapper")
    public LineMapper<Nem12RecordDto> nem12LineMapper(
            @Qualifier("nem12LineTokenizer") DelimitedLineTokenizer tokenizer
    ) {
        return new Nem12LineMapper(tokenizer);
    }

    @StepScope
    @Bean("nem12ItemReader")
    public ItemReader<Nem12RecordDto> nem12ItemReader(
            @Value("#{jobParameters['filePath']}") String filePath,
            @Qualifier("nem12LineMapper") LineMapper<Nem12RecordDto> lineMapper) {

        FlatFileItemReader<Nem12RecordDto> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setResource(new FileSystemResource(filePath));

        return itemReader;
    }

    @StepScope
    @Bean("ingestItemProcessor")
    public ItemProcessor<Nem12RecordDto, MeterReadingEntity> ingestItemProcessor() {
        return new Nem12ItemProcessor();
    }

    @StepScope
    @Bean("meterReadingEntityWriter")
    public ItemWriter<MeterReadingEntity> meterReadingEntityWriter(
            EntityManagerFactory entityManagerFactory
    ) {
        JpaItemWriter<MeterReadingEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        writer.setUsePersist(true);
        writer.setClearPersistenceContext(true);

        return writer;
    }
}
