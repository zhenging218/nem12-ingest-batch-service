package com.yiuzg.flo.nem12.ingest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@MappedSuperclass
public abstract class StagingEntity
{
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "retries", nullable = false)
    private Integer retryCount;
}
