package com.yiuzg.flo.nem12.ingest.service.impl;

import com.yiuzg.flo.nem12.ingest.dto.MeterReadingDto;
import com.yiuzg.flo.nem12.ingest.dto.NmiDetailDto;
import com.yiuzg.flo.nem12.ingest.entity.impl.MeterReadingEntity;
import com.yiuzg.flo.nem12.ingest.exception.NoAssociatedDataException;
import com.yiuzg.flo.nem12.ingest.exception.NoDataException;
import com.yiuzg.flo.nem12.ingest.exception.NoDataInRangeException;
import com.yiuzg.flo.nem12.ingest.repository.MeterReadingRepository;
import com.yiuzg.flo.nem12.ingest.service.MeterReadingService;
import com.yiuzg.flo.nem12.ingest.utilities.MeterReadingUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<MeterReadingDto> getMeterReadings() throws NoDataException
    {
        List<MeterReadingEntity> entities = meterReadingRepository.findAll();

        if(!CollectionUtils.isEmpty(entities)) {
            return entities.stream().map(MeterReadingUtil::mapEntityToDto).toList();
        }

        throw new NoDataException();
    }

    @Override
    public List<MeterReadingDto> getMeterReadingsByNmi(String nmi) throws NoAssociatedDataException
    {
        List<MeterReadingEntity> entities = meterReadingRepository.findByNmiSorted(nmi, Sort.by(Sort.Order.asc("timestamp")));

        if(!CollectionUtils.isEmpty(entities)) {
            return entities.stream().map(MeterReadingUtil::mapEntityToDto).toList();
        }

        throw new NoAssociatedDataException(nmi);
    }

    @Override
    public List<MeterReadingDto> getMeterReadingsOfRangeByNmi(String nmi, LocalDateTime start, LocalDateTime end) throws NoDataInRangeException
    {
        List<MeterReadingEntity> entities = meterReadingRepository.findByNmiAndTimestampBetweenSorted(
                nmi, start, end, Sort.by(Sort.Order.asc("timestamp")));

        if(!CollectionUtils.isEmpty(entities)) {
            return entities.stream().map(MeterReadingUtil::mapEntityToDto).toList();
        }

        throw new NoDataInRangeException(nmi, start, end);
    }

    @Override
    public List<NmiDetailDto> getListOfNmis() throws NoDataException
    {
        List<String> nmis = meterReadingRepository.findDistinctNmi();

        if(CollectionUtils.isEmpty(nmis)) {
            throw new NoDataException();
        }

        List<NmiDetailDto> result = new ArrayList<>();

        for (String nmi : nmis) {
            List<MeterReadingEntity> meterReadings = meterReadingRepository.findByNmiSorted(
                    nmi, Sort.by(Sort.Order.asc("timestamp")));

            if(!CollectionUtils.isEmpty(meterReadings)) {
                result.add(MeterReadingUtil.createNmiDetail(nmi, meterReadings.size(),
                        meterReadings.getFirst().getTimestamp(), meterReadings.getLast().getTimestamp()));
            }
        }

        return result;
    }
}
