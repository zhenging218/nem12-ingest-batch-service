package com.yiuzg.flo.nem12.ingest.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class IngestResponseDto
{
    private LocalDateTime startTime;
}
