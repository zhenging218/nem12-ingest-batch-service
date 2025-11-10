package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.constants.Nem12Constants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.mapper.Nem12FieldSetMapper;
import com.yiuzg.flo.nem12.ingest.repository.MeterReadingRepository;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import com.yiuzg.flo.nem12.ingest.writer.Nem12RecordItemWriter;
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
    public Step ingestStep(
            @Value("${flo.nem12.ingest.step.name}") String stepName,
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("nem12ItemReader") ItemReader<Nem12RecordDto> itemReader,
            @Qualifier("nem12ItemWriter") ItemWriter<Nem12RecordDto> itemWriter,
            @Value("${flo.nem12.ingest.chunk-size}") int chunkSize
    ) {
        return new StepBuilder(stepName, jobRepository)
                .<Nem12RecordDto, Nem12RecordDto>chunk(chunkSize, platformTransactionManager)
                .reader(itemReader)
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
            @Qualifier("nem12LineTokenizer") LineTokenizer tokenizer,
            @Qualifier("nem12FieldSetMapper") FieldSetMapper<Nem12RecordDto> fieldSetMapper
            ) {
        DefaultLineMapper<Nem12RecordDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @StepScope
    @Bean("nem12ItemReader")
    public FlatFileItemReader<Nem12RecordDto> nem12ItemReader(
            @Value("#{jobParameters[" + BatchJobConstants.JOB_PARAM_INGEST_FILE_KEY + "]}") String ingestFile,
            @Qualifier("nem12LineMapper") LineMapper<Nem12RecordDto> lineMapper) {
        FlatFileItemReader<Nem12RecordDto> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setResource(new FileSystemResource(ingestFile));
        return itemReader;
    }

    @StepScope
    @Bean("nem12ItemWriter")
    public ItemWriter<Nem12RecordDto> nem12ItemWriter(MeterReadingRepository meterReadingRepository) {
        return new Nem12RecordItemWriter(meterReadingRepository);
    }
}
