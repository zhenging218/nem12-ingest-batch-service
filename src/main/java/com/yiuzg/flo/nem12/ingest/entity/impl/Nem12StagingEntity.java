package com.yiuzg.flo.nem12.ingest.entity.impl;

import com.yiuzg.flo.nem12.ingest.entity.StagingEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "table_nem12_staging", indexes = {@Index(name = "index_nem12_staging_type_state", columnList = "type, state")})
public class Nem12StagingEntity extends StagingEntity
{
    @Column(name = "key")
    private String key;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;
}
