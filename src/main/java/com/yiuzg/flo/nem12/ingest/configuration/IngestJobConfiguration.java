package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.decider.HasMoreFilesToProcessDecider;
import com.yiuzg.flo.nem12.ingest.dto.FileDto;
import com.yiuzg.flo.nem12.ingest.tasklet.InitialiseIngestTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.FlowStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class IngestJobConfiguration
{
    // todo: after clarification, remove staging and commit 300 records directly if consumption
    //       is to be accumulated and not commited per interval value

    @Bean("ingestJob")
    public Job ingestJob(
            @Value("${flo.nem12.ingest.job.name}") String jobName,
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("unzipStep") Step unzipStep,
            @Qualifier("ingestDecider") JobExecutionDecider decider,
            @Qualifier("ingestFlow") Flow ingestFlow) {

        var builder = new JobBuilder(jobName, jobRepository)
                .start(unzipStep)
                .next(decider)
                .on(FlowExecutionStatus.COMPLETED.getName())
                .end();

        builder.from(decider)
                .on(BatchJobConstants.FLOW_EXECUTION_STATUS_CONTINUE.getName())
                .to(ingestFlow)
                .on(BatchJobConstants.WILD_CARD_FLOW_IND).to(decider);

        return builder.end().build();
    }

    @JobScope
    @Bean("ingestDecider")
    public JobExecutionDecider ingestDecider() {
        return new HasMoreFilesToProcessDecider();
    }

    @JobScope
    @Bean("ingestAndStageFlow")
    public Flow ingestAndStageFlow(@Value("${flo.nem12.ingest.flow.name}") String flowName,
            @Qualifier("ingestInitStep") Step ingestInitStep,
            @Qualifier("ingestAndStageStep") Step ingestStep,
            @Qualifier("commitMeterReadingStep") Step commitMeterReadingStep
    ) {
        return new FlowBuilder<Flow>(flowName)
                .start(ingestInitStep)
                .next(ingestStep)
                .next(commitMeterReadingStep)
                .end();
    }

    @JobScope
    @Bean("ingestFlow")
    public Flow ingestFlow(@Value("${flo.nem12.ingest.flow.name}") String flowName,
                           @Qualifier("ingestInitStep") Step ingestInitStep,
                           @Qualifier("ingestStep") Step ingestStep
    ) {
        return new FlowBuilder<Flow>(flowName)
                .start(ingestInitStep)
                .next(ingestStep)
                .end();
    }
}
