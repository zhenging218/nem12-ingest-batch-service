package com.yiuzg.flo.nem12.ingest.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.yiuzg.flo.nem12.ingest.constants.Nem12Constants;
import com.yiuzg.flo.nem12.ingest.dto.impl.*;
import lombok.*;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "recordIndicator", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Nem12HeaderRecordDto.class, name = Nem12Constants.NEM12_HEADER_IND),
        @JsonSubTypes.Type(value = Nem12NMIDetailRecordDto.class, name = Nem12Constants.NEM12_NMI_DETAIL_IND),
        @JsonSubTypes.Type(value = Nem12IntervalDataRecordDto.class, name = Nem12Constants.NEM12_INTERVAL_DATA_IND),
        @JsonSubTypes.Type(value = Nem12IntervalEventRecordDto.class, name = Nem12Constants.NEM12_INTERVAL_EVENT_IND),
        @JsonSubTypes.Type(value = Nem12B2BDetailsRecordDto.class, name = Nem12Constants.NEM12_B2B_DETAILS_IND),
        @JsonSubTypes.Type(value = Nem12EndRecordDto.class, name = Nem12Constants.NEM12_END_IND)
})
public interface Nem12RecordDto
{
    String getRecordIndicator();
}
