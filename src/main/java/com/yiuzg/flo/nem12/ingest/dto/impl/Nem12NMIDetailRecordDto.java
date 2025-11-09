package com.yiuzg.flo.nem12.ingest.dto.impl;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Nem12NMIDetailRecordDto implements Nem12RecordDto
{
    private String recordIndicator;
    private String nmi;
    private int intervalLength;
}
