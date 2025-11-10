package com.yiuzg.flo.nem12.ingest.constants;

import lombok.experimental.UtilityClass;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;

@UtilityClass
public class BatchJobConstants
{
    public static final String JOB_CONTEXT_FILE_LIST_KEY = "fileList";
    public static final FlowExecutionStatus FLOW_EXECUTION_STATUS_CONTINUE = new FlowExecutionStatus("CONTINUE");
    public static final String JOB_CONTEXT_FILE_PATH_KEY = "filePath";
    public static final String JOB_PARAM_INGEST_FILE_KEY = "ingestFile";
    public static final String JOB_PARAM_TIMESTAMP_KEY = "timestamp";
    public static final String WILD_CARD_FLOW_IND = "*";
}
