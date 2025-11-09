package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.entity.impl.Nem12StagingEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchJobCommonConfiguration
{
    @StepScope
    @Bean("stagingItemWriter")
    public ItemWriter<Nem12StagingEntity> stagingItemWriter(
            EntityManagerFactory entityManagerFactory
    ) {
        JpaItemWriter<Nem12StagingEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        writer.setUsePersist(true);
        writer.setClearPersistenceContext(true);

        return writer;
    }
}
