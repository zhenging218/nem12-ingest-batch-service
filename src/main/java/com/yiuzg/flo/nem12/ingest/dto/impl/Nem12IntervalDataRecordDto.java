package com.yiuzg.flo.nem12.ingest.dto.impl;

import com.yiuzg.flo.nem12.ingest.dto.Nem12RecordDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Nem12IntervalDataRecordDto implements Nem12RecordDto
{
    private String recordIndicator;
    private LocalDate intervalDate;
    private List<BigDecimal> intervalValues;
    private String qualityMethod;
    public Integer reasonCode;
    public String reasonDescription;
    public LocalDateTime updateDateTime;
    public LocalDateTime msatsLoadDateTime;
}
