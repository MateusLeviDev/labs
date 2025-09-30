package br.com.icecube.application.job;

import br.com.icecube.domain.model.SalesInfo;
import br.com.icecube.domain.dto.SalesDTO;
import br.com.icecube.domain.mapper.SalesMapper;
import br.com.icecube.infrastructure.s3.S3FlatFileItemReaderFactory;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ImportSalesInfoJob {

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SalesMapper salesMapper;
    private final S3FlatFileItemReaderFactory readerFactory;

    @Bean
    public Job syncSalesJob() {
        return new JobBuilder("sync-sales-job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fromS3ToDb())
                .build();
    }

    @Bean
    public Step fromS3ToDb() {
        return new StepBuilder("from-s3-to-db", jobRepository)
                .<SalesDTO, SalesInfo>chunk(2000, transactionManager)
                .reader(salesItemReader())
                .processor(salesMapper::mapToEntity)
                .writer(jpaWriter())
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<SalesDTO> salesItemReader() {
        return readerFactory.createReader("2025",
                new String[]{"saleId", "productId", "customerId", "saleDate", "saleAmount", "location", "country"},
                SalesDTO.class);
    }

    @Bean
        public JpaItemWriter<SalesInfo> jpaWriter() {
        return new JpaItemWriterBuilder<SalesInfo>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
