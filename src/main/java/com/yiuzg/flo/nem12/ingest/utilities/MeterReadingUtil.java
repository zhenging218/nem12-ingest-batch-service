package com.yiuzg.flo.nem12.ingest.utilities;

import com.yiuzg.flo.nem12.ingest.dto.MeterReadingDto;
import com.yiuzg.flo.nem12.ingest.entity.MeterReadingEntity;
import lombok.experimental.UtilityClass;

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
}
