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
        // todo: if record is not 200/300, then return null to skip the record
        //       if record is 900, indicate in job context that end of record is present
        //       this is for finalisation step (if file ended without 900, must throw error)
        return null;
    }
}
