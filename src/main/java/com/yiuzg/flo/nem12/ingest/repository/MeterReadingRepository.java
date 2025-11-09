package com.yiuzg.flo.nem12.ingest.repository;

import com.yiuzg.flo.nem12.ingest.entity.impl.MeterReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReadingEntity, UUID>
{
}
