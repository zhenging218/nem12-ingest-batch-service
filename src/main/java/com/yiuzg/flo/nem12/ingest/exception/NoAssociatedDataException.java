package com.yiuzg.flo.nem12.ingest.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoAssociatedDataException extends Exception
{
    private final String nmi;
}
