package com.yiuzg.flo.nem12.ingest.advice;

import com.yiuzg.flo.nem12.ingest.exception.NoAssociatedDataException;
import com.yiuzg.flo.nem12.ingest.exception.NoDataException;
import com.yiuzg.flo.nem12.ingest.exception.NoDataInRangeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class MeterReadingControllerAdvice
{
    @ExceptionHandler(NoDataException.class)
    public ErrorResponse handleNoData(NoDataException e) {
        return ErrorResponse.builder(e, HttpStatus.NOT_FOUND, "No data is present").build();
    }

    @ExceptionHandler(NoAssociatedDataException.class)
    public ErrorResponse handleNoAssociatedData(NoAssociatedDataException e) {
        return ErrorResponse.builder(e, HttpStatus.NOT_FOUND, String.format("No associated data found for nmi value %s", e.getNmi())).build();
    }

    @ExceptionHandler(NoDataInRangeException.class)
    public ErrorResponse handleNoDataInRange(NoDataInRangeException e) {
        var formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return ErrorResponse.builder(e, HttpStatus.NOT_FOUND, String.format("No associated data found for nmi value %s in range %s to %s",
                e.getNmi(), formatter.format(e.getStart()), formatter.format(e.getEnd()))).build();
    }
}
