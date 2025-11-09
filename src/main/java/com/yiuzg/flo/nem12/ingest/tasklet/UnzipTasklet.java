package com.yiuzg.flo.nem12.ingest.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import com.yiuzg.flo.nem12.ingest.service.FileStagingService;
import com.yiuzg.flo.nem12.ingest.utilities.BatchJobUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class UnzipTasklet implements Tasklet
{
    private final ObjectMapper objectMapper;
    private final FileArchiveService fileArchiveService;
    private final FileStagingService fileStagingService;

    public UnzipTasklet(ObjectMapper objectMapper, FileArchiveService fileArchiveService, FileStagingService fileStagingService)
    {
        this.objectMapper = objectMapper;
        this.fileArchiveService = fileArchiveService;
        this.fileStagingService = fileStagingService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception
    {
        FileDto ingestFile = BatchJobUtil.parseJobParameterString(objectMapper,
                (String) chunkContext.getStepContext().getJobParameters()
                        .get(BatchJobConstants.JOB_PARAM_INGEST_FILE_PATH_KEY),
                FileDto.class);

        if(ingestFile != null)
        {
            log.info("Ingest file identified: {}", ingestFile);
            // unzip and save locally
            Stack<FileDto> fileList = (Stack<FileDto>) chunkContext
                    .getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().get(BatchJobConstants.JOB_CONTEXT_FILE_LIST_KEY);

            if(fileList == null) {
                fileList = new Stack<>();
            }

            try (InputStream fileInputStream = fileArchiveService.retrieve(ingestFile))
            {
                try (ZipInputStream inputStream = new ZipInputStream(fileInputStream))
                {
                    ZipEntry entry;
                    while ((entry = inputStream.getNextEntry()) != null)
                    {
                        FileDto staged = fileStagingService.stage(entry.getName(), inputStream, len -> len > 0);
                        inputStream.closeEntry();
                        fileList.push(staged);
                    }
                }
            }

            chunkContext.getStepContext()
                    .getStepExecution().getJobExecution()
                    .getExecutionContext()
                    .put(BatchJobConstants.JOB_CONTEXT_FILE_LIST_KEY, fileList);
        }
        return RepeatStatus.FINISHED;
    }
}
