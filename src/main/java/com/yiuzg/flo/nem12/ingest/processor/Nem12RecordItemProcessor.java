package com.yiuzg.flo.nem12.ingest.processor;

import com.yiuzg.flo.nem12.ingest.constants.EStagingState;
import com.yiuzg.flo.nem12.ingest.constants.Nem12Constants;
import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import com.yiuzg.flo.nem12.ingest.dto.impl.*;
import com.yiuzg.flo.nem12.ingest.entity.impl.Nem12StagingEntity;
import org.springframework.batch.item.ItemProcessor;
import tools.jackson.databind.ObjectMapper;

public class Nem12RecordItemProcessor implements ItemProcessor<Nem12RecordDto, Nem12StagingEntity>
{
    private String currentNmi;
    private int currentExpectedRecords;

    private final ObjectMapper objectMapper;

    public Nem12RecordItemProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.currentNmi = null;
        this.currentExpectedRecords = 0;
    }

    private Nem12StagingEntity process200Record(Nem12NMIDetailRecordDto nem12200Record) {
        currentNmi = nem12200Record.getNmi();
        currentExpectedRecords = Nem12Constants.NEM12_INTERVAL_COUNT_DIVIDEND / nem12200Record.getIntervalLength();
        return null;
    }

    private Nem12StagingEntity process300Record(Nem12IntervalDataRecordDto nem12300Record) {
        // todo: check how many actual records
        //       if valid, make staging entity for type 300 and return entity
        //       stage the record in payload

        Nem12StagingEntity entity = new Nem12StagingEntity();
        entity.setType(Nem12Constants.NEM12_INTERVAL_DATA_IND);
        entity.setState(EStagingState.NEW.getCode());
        entity.setKey(currentNmi);
        entity.setPayload(objectMapper.writeValueAsString(nem12300Record));
        return entity;
    }

    @Override
    public Nem12StagingEntity process(Nem12RecordDto item) throws Exception
    {
        // no need to stage anything that is not a 300 record
        return switch (item) {
            case Nem12HeaderRecordDto nem12100Record -> null;
            case Nem12NMIDetailRecordDto nem12200Record -> process200Record(nem12200Record);
            case Nem12IntervalDataRecordDto nem12300Record -> process300Record(nem12300Record);
            case Nem12IntervalEventRecordDto nem12400Record -> null;
            case Nem12B2BDetailsRecordDto nem12500Record -> null;
            case Nem12EndRecordDto nem12900Record -> null;
            default -> throw new IllegalAccessException(String.format("Unable to process item of indicator type %s",
                    item.getRecordIndicator()));
        };
    }
}
