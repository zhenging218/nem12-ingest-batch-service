package com.yiuzg.flo.nem12.ingest.entity.impl;

import com.yiuzg.flo.nem12.ingest.entity.Nem12Entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "table meter_readings", uniqueConstraints = @UniqueConstraint(columnNames = {"nmi", "timestamp"}))
public class MeterReadingEntity extends Nem12Entity
{
    @Column(name = "nmi", length = 10, nullable = false)
    private String nmi;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "consumption", nullable = false)
    private BigDecimal consumption;
}
