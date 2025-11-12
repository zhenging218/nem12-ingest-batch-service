package com.yiuzg.flo.nem12.ingest.configuration;

import com.yiuzg.flo.nem12.ingest.constants.BatchJobConstants;
import com.yiuzg.flo.nem12.ingest.service.FileArchiveService;
import com.yiuzg.flo.nem12.ingest.tranformer.JobIntegrationTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.DirectoryScanner;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

@Configuration
@Slf4j
public class FileSystemIntegrationConfiguration
{
    @Bean("nem12InboundMessageSource")
    public MessageSource<File> messageSource(
            @Value("${flo.nem12.ingest.staging.file-system-local.location}") String stagingDirectory,
            @Value("${flo.nem12.ingest.staging.file-system-local.location.auto-create}") boolean autoCreateDirectory,
            @Qualifier("nem12DirectoryScanner") DirectoryScanner directoryScanner)
    {
        FileReadingMessageSource messageSource = new FileReadingMessageSource();
        messageSource.setDirectory(Path.of(stagingDirectory).toAbsolutePath().toFile());
        messageSource.setAutoCreateDirectory(true);
        messageSource.setScanner(directoryScanner);
        messageSource.setAutoCreateDirectory(autoCreateDirectory);

        return messageSource;
    }

    @Bean("nem12FileListFilter")
    public CompositeFileListFilter<File> fileListFilter(
            @Value("${flo.nem12.ingest.staging.file-system-local.pattern}") String pattern
    ) {
        return new CompositeFileListFilter<>(
                List.of(new SimplePatternFileListFilter(pattern))
        );
    }

    @Bean("nem12DirectoryScanner")
    public DirectoryScanner directoryScanner(
            @Qualifier("nem12FileListFilter") CompositeFileListFilter<File> fileListFilter) {
        DirectoryScanner scanner = new RecursiveDirectoryScanner();
        scanner.setFilter(fileListFilter);
        return scanner;
    }

    @Bean("nem12JobResultChannel")
    public MessageChannel resultChannel()
    {
        return new DirectChannel();
    }

    @Bean("nem12JobLaunchingGateway")
    public JobLaunchingGateway jobLaunchingGateway(
            JobRepository jobRepository,
            @Qualifier("nem12JobResultChannel") MessageChannel resultChannel) {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SyncTaskExecutor());

        var gateway = new JobLaunchingGateway(jobLauncher);
        gateway.setOutputChannel(resultChannel);

        return gateway;
    }

    @Bean("ingestIntegrationFlow")
    public IntegrationFlow ingestIntegrationFlow(
            @Value("${flo.nem12.ingest.staging.file-system-local.location.poll-rate-millis}") long pollRateMillis,
            @Qualifier("nem12InboundMessageSource") MessageSource<File> messageSource,
            JobIntegrationTransformer transformer,
            @Qualifier("nem12JobLaunchingGateway") JobLaunchingGateway jobLaunchingGateway) {
        return IntegrationFlow.from(messageSource,
                        c ->
                                c.poller(Pollers.fixedRate(Duration.ofMillis(pollRateMillis)).maxMessagesPerPoll(1))).
                transform(transformer).
                handle(jobLaunchingGateway).
                get();
    }

    @Bean("ingestResultIntegrationFlow")
    public IntegrationFlow ingestResultIntegrationFlow(
            FileArchiveService fileArchiveService,
            @Qualifier("nem12JobResultChannel") MessageChannel resultChannel) {
        return IntegrationFlow.from(resultChannel)
                .handle(message -> {
                    JobExecution execution = (JobExecution) message.getPayload();
                    String ingestFile = execution.getJobParameters().getString(BatchJobConstants.JOB_PARAM_INGEST_FILE_KEY);

                    log.info("JobExecution completed for file {} with status {}",
                            ingestFile, execution.getStatus());

                    if(StringUtils.isNotBlank(ingestFile))
                    {
                        try
                        {
                            fileArchiveService.archive(Path.of(ingestFile).toAbsolutePath().toFile());
                        }
                        catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }).get();
    }
}
