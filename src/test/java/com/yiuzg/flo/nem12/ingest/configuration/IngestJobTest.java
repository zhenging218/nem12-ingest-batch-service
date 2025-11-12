package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;

@SpringBatchTest
@SpringBootTest
class IngestJobTest
{
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    @Qualifier("ingestJob")
    private Job ingestJob;

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
    }

    @Test
    void testIngestFileSuccess() throws Exception {

        var result = jobLauncherTestUtils.launchJob(createJobParameters(new ClassPathResource("payload/data.csv").getFile().getAbsolutePath()));

        Assertions.assertEquals(ExitStatus.COMPLETED.getExitCode(), result.getExitStatus().getExitCode());

    }
}
