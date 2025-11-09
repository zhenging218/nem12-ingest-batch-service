package com.yiuzg.flo.nem12.ingest.decider;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Stack;

public class HasMoreFilesToProcessDecider implements JobExecutionDecider
{
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution)
    {
        Stack<FileDto> filesToProcess = (Stack<FileDto>) jobExecution.getExecutionContext().get(BatchJobConstants.JOB_CONTEXT_FILE_LIST_KEY);

        if(!CollectionUtils.isEmpty(filesToProcess)) {
            return BatchJobConstants.FLOW_EXECUTION_STATUS_CONTINUE;
        }

        return FlowExecutionStatus.COMPLETED;
    }
}
