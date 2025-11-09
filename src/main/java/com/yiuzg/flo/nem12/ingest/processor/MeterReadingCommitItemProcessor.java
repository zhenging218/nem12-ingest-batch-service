package com.yiuzg.flo.nem12.ingest.processor;

import com.yiuzg.flo.nem12.ingest.constants.EStagingState;
import com.yiuzg.flo.nem12.ingest.dto.impl.Nem12IntervalDataRecordDto;
import com.yiuzg.flo.nem12.ingest.entity.impl.MeterReadingEntity;
import com.yiuzg.flo.nem12.ingest.entity.impl.Nem12StagingEntity;
import com.yiuzg.flo.nem12.ingest.repository.MeterReadingRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Collectors;

public class MeterReadingCommitItemProcessor implements ItemProcessor<Nem12StagingEntity, Nem12StagingEntity>
{
    private final ObjectMapper objectMapper;
    private final MeterReadingRepository meterReadingRepository;

    public MeterReadingCommitItemProcessor(ObjectMapper objectMapper, MeterReadingRepository meterReadingRepository)
    {
        this.objectMapper = objectMapper;
        this.meterReadingRepository = meterReadingRepository;
    }

    @Override
    public Nem12StagingEntity process(Nem12StagingEntity item) throws Exception
    {
        Nem12IntervalDataRecordDto data = objectMapper.readValue(item.getPayload(), Nem12IntervalDataRecordDto.class);

        if(!CollectionUtils.isEmpty(data.getIntervalValues())) {
            meterReadingRepository.saveAll(data.getIntervalValues().stream()
                    .map(intervalValue ->
                            new MeterReadingEntity(item.getKey(), data.getIntervalDate().atStartOfDay(), intervalValue))
                    .collect(Collectors.toSet()));
        }

        item.setState(EStagingState.SUCCESS.getCode());
        return item;
    }
}
