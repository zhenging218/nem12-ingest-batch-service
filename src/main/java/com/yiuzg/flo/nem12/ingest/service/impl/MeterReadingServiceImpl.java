package com.yiuzg.flo.nem12.ingest.service.impl;

import com.yiuzg.flo.nem12.ingest.dto.MeterReadingDto;
import com.yiuzg.flo.nem12.ingest.repository.MeterReadingRepository;
import com.yiuzg.flo.nem12.ingest.service.MeterReadingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MeterReadingServiceImpl implements MeterReadingService
{
    private final MeterReadingRepository meterReadingRepository;

    @Autowired
    public MeterReadingServiceImpl(MeterReadingRepository meterReadingRepository)
    {
        this.meterReadingRepository = meterReadingRepository;
    }

    @Override
    public List<MeterReadingDto> getMeterReadings()
    {
        return List.of();
    }

    @Override
    public List<MeterReadingDto> getMeterReadingsByNmi(String nmi)
    {
        return List.of();
    }

    @Override
    public List<MeterReadingDto> getMeterReadingsOfRange(LocalDateTime start, LocalDateTime end)
    {
        return List.of();
    }

    @Override
    public List<MeterReadingDto> getMeterReadingsOfRangeByNmi(String nmi, LocalDateTime start, LocalDateTime end)
    {
        return List.of();
    }
}
