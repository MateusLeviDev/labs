package br.com.icecube.job;

import br.com.icecube.batch.S3SalesItemReader;
import br.com.icecube.dto.SalesDTO;
import br.com.icecube.mapper.SalesMapper;
import br.com.icecube.service.CustomS3Client;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import br.com.icecube.domain.SalesInfo;
import br.com.icecube.task.DownloadFileTask;

import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ImportSalesInfoJob {

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SalesMapper salesMapper;
    private final DownloadFileTask downloadFileTask;
    private final CustomS3Client customS3Client;

    @Bean
    public Job syncSalesJob(Step downloadFileStep, Step fromFileDownloadedToDb) {
        return new JobBuilder("sync-sales-job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(downloadFileStep)
                .next(fromFileDownloadedToDb)
                .end()
                .build();
    }

    @Bean
    public Step downloadFileStep() {
        return new StepBuilder("downloadFileStep", jobRepository)
                .tasklet(downloadFileTask, transactionManager)
                .build();
    }

    @Bean
    public Step fromFileDownloadedToDb(S3SalesItemReader s3SalesItemReader) {
        return new StepBuilder("fromFileDownloadedToDb", jobRepository)
                .<SalesDTO, SalesInfo>chunk(2000, transactionManager)
                .reader(s3SalesItemReader)
                .processor(salesMapper::mapToEntity)
                .writer(salesJpaWriter())
                .build();
    }

    @Bean
    @JobScope
    public S3SalesItemReader s3SalesItemReader(
            @Value("#{jobExecutionContext['input.file.keys']}") List<String> keys) {
        return new S3SalesItemReader(customS3Client, keys);
    }

    @Bean
    public JpaItemWriter<SalesInfo> salesJpaWriter() {
        return new JpaItemWriterBuilder<SalesInfo>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
