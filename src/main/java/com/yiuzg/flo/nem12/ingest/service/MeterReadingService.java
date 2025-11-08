package com.yiuzg.flo.nem12.ingest.service;

import com.yiuzg.flo.nem12.ingest.dto.MeterReadingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface MeterReadingService
{
    List<MeterReadingDto> getMeterReadings();
    List<MeterReadingDto> getMeterReadingsByNmi(String nmi);
    List<MeterReadingDto> getMeterReadingsOfRange(LocalDateTime start, LocalDateTime end);
    List<MeterReadingDto> getMeterReadingsOfRangeByNmi(String nmi, LocalDateTime start, LocalDateTime end);
}
