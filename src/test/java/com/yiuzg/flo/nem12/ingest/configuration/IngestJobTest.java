package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.repository.MeterReadingRepository;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBatchTest
@SpringBootTest
class IngestJobTest
{
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockitoBean
    private FileArchiveService fileArchiveService;

    @Autowired
    @Qualifier("ingestJob")
    private Job ingestJob;

    @MockitoSpyBean
    private MeterReadingRepository meterReadingRepository;

    @Value("${flo.nem12.ingest.step.name}")
    private String ingestStepName;

    @Value("${flo.nem12.ingest.init.step.name}")
    private String ingestInitStepName;

    private JobParameters createJobParameters(String ingestFile) {
        JobParametersBuilder builder = new JobParametersBuilder();
        if(ingestFile != null)
        {
            builder.addString(BatchJobConstants.JOB_PARAM_INGEST_FILE_KEY, ingestFile);
        }
        builder.addLocalDateTime(BatchJobConstants.JOB_PARAM_TIMESTAMP_KEY, LocalDateTime.now());
        return builder.toJobParameters();
    }

    @BeforeEach
    void setup() {
        jobLauncherTestUtils.setJob(ingestJob);
        meterReadingRepository.deleteAll();
    }

    @Test
    void testIngestFileSuccess() throws Exception {

        var result = jobLauncherTestUtils.launchJob(createJobParameters(new ClassPathResource("payload/data.csv").getFile().getAbsolutePath()));

        Assertions.assertTrue(result.getStepExecutions().stream().map(StepExecution::getStepName)
                .toList().containsAll(List.of(ingestInitStepName, ingestStepName)));

        Assertions.assertEquals(ExitStatus.COMPLETED.getExitCode(), result.getExitStatus().getExitCode());

        Mockito.verify(meterReadingRepository, Mockito.times(8)).save(any());
    }

    @Test
    void testIngestNoFileSuccess() throws Exception {

        var result = jobLauncherTestUtils.launchJob(createJobParameters(null));

        var executedSteps = result.getStepExecutions().stream().map(StepExecution::getStepName).toList();
        Assertions.assertTrue(executedSteps.contains(ingestInitStepName));
        Assertions.assertFalse(executedSteps.contains(ingestStepName));
        Assertions.assertEquals(ExitStatus.COMPLETED.getExitCode(), result.getExitStatus().getExitCode());

        Mockito.verify(meterReadingRepository, Mockito.times(0)).save(any());
    }

    @Test
    void testIngestIncorrectIntervalValueCount() throws Exception {

        var result = jobLauncherTestUtils.launchJob(createJobParameters(new ClassPathResource("payload/data_incorrect_interval_value_count.csv").getFile().getAbsolutePath()));

        Assertions.assertTrue(result.getStepExecutions().stream().map(StepExecution::getStepName)
                .toList().containsAll(List.of(ingestInitStepName, ingestStepName)));

        Assertions.assertEquals(ExitStatus.FAILED.getExitCode(), result.getExitStatus().getExitCode());

        Mockito.verify(meterReadingRepository, Mockito.times(2)).save(any());
    }

    @Test
    void testIngestDuplicateData() throws Exception {

        var result = jobLauncherTestUtils.launchJob(createJobParameters(new ClassPathResource("payload/duplicate_data.csv").getFile().getAbsolutePath()));

        Assertions.assertTrue(result.getStepExecutions().stream().map(StepExecution::getStepName)
                .toList().containsAll(List.of(ingestInitStepName, ingestStepName)));

        Assertions.assertEquals(ExitStatus.COMPLETED.getExitCode(), result.getExitStatus().getExitCode());

        Mockito.verify(meterReadingRepository, Mockito.times(4)).save(any());
    }

    @Test
    void testIngestMissing200Record() throws Exception {

        var result = jobLauncherTestUtils.launchJob(createJobParameters(new ClassPathResource("payload/data_missing_200_record.csv").getFile().getAbsolutePath()));

        Assertions.assertTrue(result.getStepExecutions().stream().map(StepExecution::getStepName)
                .toList().containsAll(List.of(ingestInitStepName, ingestStepName)));

        Assertions.assertEquals(ExitStatus.FAILED.getExitCode(), result.getExitStatus().getExitCode());

        Mockito.verify(meterReadingRepository, Mockito.times(0)).save(any());
    }

    @Test
    void testIngestRecordsAfter900() throws Exception {

        var result = jobLauncherTestUtils.launchJob(createJobParameters(new ClassPathResource("payload/data_more_records_after_900.csv").getFile().getAbsolutePath()));

        Assertions.assertTrue(result.getStepExecutions().stream().map(StepExecution::getStepName)
                .toList().containsAll(List.of(ingestInitStepName, ingestStepName)));

        Assertions.assertEquals(ExitStatus.FAILED.getExitCode(), result.getExitStatus().getExitCode());

        Mockito.verify(meterReadingRepository, Mockito.times(4)).save(any());
    }
}
