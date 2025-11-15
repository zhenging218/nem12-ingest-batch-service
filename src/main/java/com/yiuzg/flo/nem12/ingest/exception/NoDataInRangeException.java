package com.yiuzg.flo.nem12.ingest.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NoDataInRangeException extends Exception
{
    private final String nmi;

    private final LocalDateTime start;
    private final LocalDateTime end;
}
