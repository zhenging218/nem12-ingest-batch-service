package com.yiuzg.flo.nem12.ingest.repository;

import com.yiuzg.flo.nem12.ingest.entity.impl.Nem12StagingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Nem12StagingRepository extends JpaRepository<Nem12StagingEntity, String>
{
}
