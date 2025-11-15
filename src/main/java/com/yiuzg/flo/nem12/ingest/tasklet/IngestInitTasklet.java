package com.yiuzg.flo.nem12.ingest.tasklet;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
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
            contribution.setExitStatus(ExitStatus.COMPLETED);
        } else {
            log.info("no file to ingest");
            contribution.setExitStatus(ExitStatus.NOOP);
        }

        return RepeatStatus.FINISHED;
    }
}
