package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.entity.impl.Nem12StagingEntity;
import com.yiuzg.flo.nem12.ingest.mapper.Nem12FieldSetMapper;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
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
       @Qualifier("ingestItemReader") ItemReader<Nem12RecordDto> itemReader,
       @Qualifier("ingestItemProcessor") ItemProcessor<Nem12RecordDto, Nem12StagingEntity> itemProcessor,
       @Qualifier("stagingItemWriter") ItemWriter<Nem12StagingEntity> itemWriter,
       @Value("${flo.nem12.ingest.chunk-size}") int chunkSize
    ) {
        return new StepBuilder(stepName, jobRepository)
                .<Nem12RecordDto, Nem12StagingEntity>chunk(chunkSize, platformTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @StepScope
    @Bean("nem12FieldSetMapper")
    public FieldSetMapper<Nem12RecordDto> nem12FieldSetMapper() {
        return new Nem12FieldSetMapper();
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
            @Qualifier("nem12LineTokenizer") DelimitedLineTokenizer tokenizer,
            @Qualifier("nem12FieldSetMapper") FieldSetMapper<Nem12RecordDto> fieldSetMapper
            ) {
        DefaultLineMapper<Nem12RecordDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @StepScope
    @Bean("ingestItemReader")
    public ItemReader<Nem12RecordDto> ingestItemReader(
            @Value("#{jobParameters['filePath']}") String filePath,
            @Qualifier("nem12LineMapper") LineMapper<Nem12RecordDto> lineMapper) {

        FlatFileItemReader<Nem12RecordDto> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setResource(new FileSystemResource(filePath));

        return itemReader;
    }
}
