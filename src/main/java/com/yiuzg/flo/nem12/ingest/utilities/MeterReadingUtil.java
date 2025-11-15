package com.yiuzg.flo.nem12.ingest.utilities;

import com.yiuzg.flo.nem12.ingest.dto.MeterReadingDto;
import com.yiuzg.flo.nem12.ingest.dto.NmiDetailDto;
import com.yiuzg.flo.nem12.ingest.entity.impl.MeterReadingEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class MeterReadingUtil
{
    public static MeterReadingDto mapEntityToDto(MeterReadingEntity entity) {

        return new MeterReadingDto(entity.getNmi(), entity.getTimestamp(), entity.getConsumption());
    }

    public static void populateEntityWithDto(MeterReadingEntity entity, MeterReadingDto dto) {
        entity.setNmi(dto.getNmi());
        entity.setTimestamp(dto.getTimestamp());
        entity.setConsumption(dto.getConsumption());
    }

    public static NmiDetailDto createNmiDetail(String nmi, int count, LocalDateTime start, LocalDateTime end)
    {
        return new NmiDetailDto(nmi, count, start, end);
    }
}
