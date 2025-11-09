package com.yiuzg.flo.nem12.ingest.query;

import com.yiuzg.flo.nem12.ingest.constants.EStagingState;
import com.yiuzg.flo.nem12.ingest.entity.impl.Nem12StagingEntity;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Stream;

public class Nem12StagingQueryProvider extends AbstractJpaQueryProvider
{
    private final String type;
    private final List<String> states;
    private final String groupBy;

    public Nem12StagingQueryProvider(String type, String groupBy, EStagingState ... states)
    {
        this.type = type;
        this.groupBy = groupBy;
        this.states = Stream.of(states).map(EStagingState::getCode).toList();
    }

    public Nem12StagingQueryProvider(String type, EStagingState ... states)
    {
        this.type = type;
        this.states = Stream.of(states).map(EStagingState::getCode).toList();
        this.groupBy = null;
    }

    @Override
    public Query createQuery()
    {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Nem12StagingEntity> cq = cb.createQuery(Nem12StagingEntity.class);
        Root<Nem12StagingEntity> root = cq.from(Nem12StagingEntity.class);

        cq.select(root)
                .where(
                cb.equal(root.get("type"), type),
                root.get("state").in(states)
        );

        if(!StringUtils.isEmpty(groupBy)) {
            cq.groupBy(root.get(groupBy));
        }

        return getEntityManager().createQuery(cq);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(getEntityManager(), "EntityManager must not be null");
    }
}
