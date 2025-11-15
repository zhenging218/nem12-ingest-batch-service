package com.yiuzg.flo.nem12.ingest.exception;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DuplicateDataException extends Exception
{
    private final Nem12RecordDto duplicate;
}
