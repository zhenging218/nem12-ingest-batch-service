package com.yiuzg.flo.nem12.ingest.reader;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.service.FileStagingService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;

public class IngestLocalFileItemReader extends FlatFileItemReader<Nem12RecordDto> implements StepExecutionListener
{
    private final FileStagingService fileStagingService;

    public IngestLocalFileItemReader(FileStagingService fileStagingService, LineMapper<Nem12RecordDto> lineMapper)
    {
        super();
        this.fileStagingService = fileStagingService;
        setLineMapper(lineMapper);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        FileDto file = (FileDto) stepExecution.getJobExecution().getExecutionContext().get(BatchJobConstants.JOB_CONTEXT_FILE_PATH_KEY);
        try
        {
            setResource(new InputStreamResource(fileStagingService.retrieve(file)));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
