package com.yiuzg.flo.nem12.ingest.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class IngestInitTasklet implements Tasklet
{
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception
    {
        String ingestFilePath = chunkContext.getStepContext().getStepExecution().
                getJobExecution().getJobParameters().getString(BatchJobConstants.JOB_PARAM_INGEST_FILE_KEY);

        if(!StringUtils.isEmpty(ingestFilePath)) {
            chunkContext.getStepContext().getStepExecution().
                    getJobExecution().getExecutionContext()
                    .put(BatchJobConstants.JOB_CONTEXT_FILE_PATH_KEY, ingestFilePath);
        }

        return RepeatStatus.FINISHED;
    }
}
