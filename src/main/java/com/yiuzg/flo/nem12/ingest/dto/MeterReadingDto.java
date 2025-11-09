package com.yiuzg.flo.nem12.ingest.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MeterReadingDto implements Serializable
{
    private String nmi;
    private LocalDateTime timestamp;
    private BigDecimal consumption;
}
