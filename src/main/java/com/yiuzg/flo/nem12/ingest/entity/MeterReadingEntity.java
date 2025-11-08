package com.yiuzg.flo.nem12.ingest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "table meter_readings", uniqueConstraints = @UniqueConstraint(columnNames = {"nmi", "timestamp"}))
public class MeterReadingEntity
{
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nmi", length = 10, nullable = false)
    private String nmi;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "consumption", nullable = false)
    private BigDecimal consumption;
}
