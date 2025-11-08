package com.yiuzg.flo.nem12.ingest.processor;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.entity.MeterReadingEntity;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@NoArgsConstructor
public class Nem12ItemProcessor implements ItemProcessor<Nem12RecordDto, MeterReadingEntity>
{
    @Override
    public MeterReadingEntity process(Nem12RecordDto item) throws Exception
    {
        return null;
    }
}
