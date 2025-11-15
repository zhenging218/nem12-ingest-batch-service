package com.yiuzg.flo.nem12.ingest.controller;

import com.yiuzg.flo.nem12.ingest.dto.MeterReadingDto;
import com.yiuzg.flo.nem12.ingest.dto.NmiDetailDto;
import com.yiuzg.flo.nem12.ingest.exception.NoAssociatedDataException;
import com.yiuzg.flo.nem12.ingest.exception.NoDataException;
import com.yiuzg.flo.nem12.ingest.exception.NoDataInRangeException;
import com.yiuzg.flo.nem12.ingest.service.MeterReadingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/flo/nem12")
public class MeterReadingController
{
    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService)
    {
        this.meterReadingService = meterReadingService;
    }

    @GetMapping
    public List<MeterReadingDto> getAllReadings() throws NoDataException
    {
        return meterReadingService.getMeterReadings();
    }

    @GetMapping(path = {"/nmi", "/nmi/"})
    public List<NmiDetailDto> getNmiDetails() throws NoDataException
    {
        return meterReadingService.getListOfNmis();
    }

    @GetMapping(path = {"/reading", "/reading/"})
    public List<MeterReadingDto> getMeterReadings(@RequestParam("nmi") String nmi) throws NoAssociatedDataException
    {
        return meterReadingService.getMeterReadingsByNmi(nmi);
    }

    @GetMapping(path = {"/reading/range", "/reading/range/"})
    public List<MeterReadingDto> getMeterReadingsTimestampsBetween(
            @RequestParam("nmi") String nmi,
            @RequestParam("start") @DateTimeFormat(pattern = "yyyyMMddHHmmss") LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyyMMddHHmmss") LocalDateTime end) throws NoDataInRangeException
    {
        return meterReadingService.getMeterReadingsOfRangeByNmi(nmi, start, end);
    }
}
