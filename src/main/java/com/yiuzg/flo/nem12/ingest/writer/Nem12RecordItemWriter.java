package com.yiuzg.flo.nem12.ingest.writer;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.dto.impl.Nem12IntervalDataRecordDto;
import com.yiuzg.flo.nem12.ingest.dto.impl.Nem12NMIDetailRecordDto;
import com.yiuzg.flo.nem12.ingest.entity.impl.MeterReadingEntity;
import com.yiuzg.flo.nem12.ingest.exception.DuplicateDataException;
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

    private MeterReadingEntity createMeterReadingForCommit(Nem12IntervalDataRecordDto nem12300Record) throws DuplicateDataException
    {
        BigDecimal consumption = nem12300Record.getIntervalValues().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime timestamp = nem12300Record.getIntervalDate().atStartOfDay();

        Optional<MeterReadingEntity> result = meterReadingRepository.findByNmiAndTimestamp(nem12300Record.getNmi(), timestamp);

        if(result.isEmpty()) {
            MeterReadingEntity entity = new MeterReadingEntity();
            entity.setNmi(nem12300Record.getNmi());
            entity.setTimestamp(timestamp);
            entity.setConsumption(consumption);
            return entity;
        }

        log.error("Duplicate record found: {}", nem12300Record);
        throw new DuplicateDataException(nem12300Record);
    }

    public Nem12RecordItemWriter(MeterReadingRepository meterReadingRepository)
    {
        this.meterReadingRepository = meterReadingRepository;
    }

    @Override
    public void write(Chunk<? extends Nem12RecordDto> chunk) throws Exception
    {
        if(!chunk.isEmpty()) {
            for (Nem12RecordDto item : chunk.getItems())
            {
                switch (item) {
                    // intentionally left as switch case, so that implementing the other record types no need
                    // to rewrite if statement
                    case Nem12IntervalDataRecordDto nem12300Record ->
                            meterReadingRepository.save(createMeterReadingForCommit(nem12300Record));
                    default -> {}
                }
            }
        }
    }
}
