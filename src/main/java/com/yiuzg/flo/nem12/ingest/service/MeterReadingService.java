package com.yiuzg.flo.nem12.ingest.service;

import com.yiuzg.flo.nem12.ingest.dto.MeterReadingDto;
import com.yiuzg.flo.nem12.ingest.dto.NmiDetailDto;
import com.yiuzg.flo.nem12.ingest.exception.NoAssociatedDataException;
import com.yiuzg.flo.nem12.ingest.exception.NoDataException;
import com.yiuzg.flo.nem12.ingest.exception.NoDataInRangeException;

import java.time.LocalDateTime;
import java.util.List;

public interface MeterReadingService
{
    List<MeterReadingDto> getMeterReadings() throws NoDataException;
    List<MeterReadingDto> getMeterReadingsByNmi(String nmi) throws NoAssociatedDataException;
    List<MeterReadingDto> getMeterReadingsOfRangeByNmi(String nmi, LocalDateTime start, LocalDateTime end) throws NoDataInRangeException;

    List<NmiDetailDto> getListOfNmis() throws NoDataException;
}
