package com.yiuzg.flo.nem12.ingest.repository;

import com.yiuzg.flo.nem12.ingest.entity.impl.MeterReadingEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReadingEntity, UUID>
{
    @Query("SELECT DISTINCT m.nmi FROM MeterReadingEntity m")
    List<String> findDistinctNmi();

    @Query("SELECT m FROM MeterReadingEntity m WHERE m.nmi = ?1 AND m.timestamp = ?2")
    Optional<MeterReadingEntity> findByNmiAndTimestamp(String nmi, LocalDateTime timestamp);

    @Query("SELECT m FROM MeterReadingEntity m WHERE m.nmi = ?1 AND m.timestamp BETWEEN ?2 AND ?3")
    List<MeterReadingEntity> findByNmiAndTimestampBetweenSorted(String nmi, LocalDateTime from, LocalDateTime to, Sort sortBy);

    @Query("SELECT m FROM MeterReadingEntity m WHERE m.nmi = ?1")
    List<MeterReadingEntity> findByNmiSorted(String nmi, Sort sortBy);
}
