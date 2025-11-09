package com.yiuzg.flo.nem12.ingest.tasklet;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Stack;

public class InitialiseIngestTasklet implements Tasklet
{
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception
    {
        Stack<FileDto> fileList = (Stack<FileDto>) chunkContext.getStepContext()
                .getStepExecution().getJobExecution().getExecutionContext()
                .get(BatchJobConstants.JOB_CONTEXT_FILE_LIST_KEY);

        if(!CollectionUtils.isEmpty(fileList))
        {
            FileDto filePath = fileList.pop();
            chunkContext.getStepContext()
                    .getStepExecution().getJobExecution()
                    .getExecutionContext().put(BatchJobConstants.JOB_CONTEXT_FILE_PATH_KEY, filePath);

            chunkContext.getStepContext()
                    .getStepExecution().getJobExecution()
                    .getExecutionContext().put(BatchJobConstants.JOB_CONTEXT_FILE_LIST_KEY, fileList);
        } else {
            throw new IllegalArgumentException("Expected file list to not be empty");
        }
        return RepeatStatus.FINISHED;
    }
}
