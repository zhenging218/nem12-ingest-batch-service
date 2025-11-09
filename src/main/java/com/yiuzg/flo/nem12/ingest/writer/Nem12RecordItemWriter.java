package com.yiuzg.flo.nem12.ingest.writer;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.dto.impl.Nem12IntervalDataRecordDto;
import com.yiuzg.flo.nem12.ingest.dto.impl.Nem12NMIDetailRecordDto;
import com.yiuzg.flo.nem12.ingest.entity.impl.MeterReadingEntity;
import com.yiuzg.flo.nem12.ingest.repository.MeterReadingRepository;
import org.apache.commons.lang3.Strings;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.math.BigDecimal;

public class Nem12RecordItemWriter implements ItemWriter<Nem12RecordDto>
{
    private final MeterReadingRepository meterReadingRepository;

    private String currentNmi;

    private void commitMeterReading(Nem12IntervalDataRecordDto nem12300Record) {
        BigDecimal consumption = nem12300Record.getIntervalValues().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        meterReadingRepository.save(
                new MeterReadingEntity(currentNmi, nem12300Record.getIntervalDate().atStartOfDay(), consumption));
    }

    public Nem12RecordItemWriter(MeterReadingRepository meterReadingRepository)
    {
        this.meterReadingRepository = meterReadingRepository;
        this.currentNmi = null;
    }

    @Override
    public void write(Chunk<? extends Nem12RecordDto> chunk) throws Exception
    {
        if(!chunk.isEmpty()) {
            for (Nem12RecordDto item : chunk.getItems())
            {
                switch (item) {
                    case Nem12NMIDetailRecordDto nem12200Record -> currentNmi = nem12200Record.getNmi();
                    case Nem12IntervalDataRecordDto nem12300Record -> commitMeterReading(nem12300Record);
                    default -> {}
                }
            }
        }
    }
}
