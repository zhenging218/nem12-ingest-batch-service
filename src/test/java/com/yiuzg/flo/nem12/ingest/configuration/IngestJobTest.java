package com.yiuzg.flo.nem12.ingest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.repository.MeterReadingRepository;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import com.yiuzg.flo.nem12.ingest.utilities.BatchJobUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.zip.ZipOutputStream;

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

    private JobParameters createJobParameters(FileDto ingestFile) {
        JobParametersBuilder builder = new JobParametersBuilder();
        if(ingestFile != null)
        {
            builder.addString(BatchJobConstants.JOB_PARAM_INGEST_FILE_KEY, ingestFile.getObjectKey());
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
        var ingestFile = new FileDto("data.csv", new ClassPathResource("payload/data.csv").getFile().getAbsolutePath());

        var result = jobLauncherTestUtils.launchJob(createJobParameters(ingestFile));

        Assertions.assertEquals(ExitStatus.COMPLETED.getExitCode(), result.getExitStatus().getExitCode());

    }
}
