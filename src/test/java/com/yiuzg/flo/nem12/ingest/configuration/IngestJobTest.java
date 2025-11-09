package com.yiuzg.flo.nem12.ingest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import com.yiuzg.flo.nem12.ingest.utilities.BatchJobUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.zip.ZipOutputStream;

import static org.mockito.ArgumentMatchers.any;

@SpringBatchTest
@SpringBootTest
class IngestJobTest
{
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("ingestJob")
    private Job ingestJob;

    @MockitoBean
    private FileArchiveService fileArchiveService;

    private JobParameters createJobParameters(FileDto ingestFile) {
        JobParametersBuilder builder = new JobParametersBuilder();
        if(ingestFile != null)
        {
            builder.addString(BatchJobConstants.JOB_PARAM_INGEST_FILE_PATH_KEY,
                    BatchJobUtil.createJobParameterString(objectMapper, ingestFile));
        }
        builder.addLocalDateTime(BatchJobConstants.JOB_PARAM_TIMESTAMP_KEY, LocalDateTime.now());
        return builder.toJobParameters();
    }

    @Test
    void testIngestJobNoIngestFileSuccess() throws Exception
    {
        var result = jobLauncher.run(ingestJob, createJobParameters(null));
        Assertions.assertEquals(ExitStatus.COMPLETED, result.getExitStatus());
    }

    @Test
    void testIngestJobEmptyIngestFileSuccess() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(ZipOutputStream outputStream = new ZipOutputStream(baos)) {}


        Mockito.when(fileArchiveService.retrieve(any(FileDto.class)))
                .thenReturn(new ByteArrayInputStream(baos.toByteArray()));

        var result = jobLauncher.run(ingestJob, createJobParameters(new FileDto("test", "test")));
        Assertions.assertEquals(ExitStatus.COMPLETED, result.getExitStatus());
    }
}
