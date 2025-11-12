package com.yiuzg.flo.nem12.ingest.writer;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.dto.impl.Nem12IntervalDataRecordDto;
import com.yiuzg.flo.nem12.ingest.dto.impl.Nem12NMIDetailRecordDto;
import com.yiuzg.flo.nem12.ingest.entity.impl.MeterReadingEntity;
import com.yiuzg.flo.nem12.ingest.repository.MeterReadingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class Nem12RecordItemWriter implements ItemWriter<Nem12RecordDto>
{
    private final MeterReadingRepository meterReadingRepository;

    private String currentNmi;

    private Optional<MeterReadingEntity> createMeterReadingForCommit(Nem12IntervalDataRecordDto nem12300Record) {
        BigDecimal consumption = nem12300Record.getIntervalValues().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime timestamp = nem12300Record.getIntervalDate().atStartOfDay();

        Optional<MeterReadingEntity> result = meterReadingRepository.findByNmiAndTimestamp(currentNmi, timestamp);

        if(result.isEmpty()) {
            MeterReadingEntity entity = new MeterReadingEntity();
            entity.setNmi(currentNmi);
            entity.setTimestamp(timestamp);
            entity.setConsumption(consumption);
            return Optional.of(entity);
        }

        log.warn("Duplicate record found (will skip): {}", nem12300Record);
        return Optional.empty();
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
                    case Nem12IntervalDataRecordDto nem12300Record -> createMeterReadingForCommit(nem12300Record)
                            .ifPresent(meterReadingRepository::save);
                    default -> {}
                }
            }
        }
    }
}
