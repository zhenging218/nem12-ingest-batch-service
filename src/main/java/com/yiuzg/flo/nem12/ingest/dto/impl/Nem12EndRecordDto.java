package com.yiuzg.flo.nem12.ingest.dto.impl;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Nem12EndRecordDto implements Nem12RecordDto
{
    private String recordIndicator;
}
