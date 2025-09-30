package br.com.icecube.infrastructure.s3;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class S3FlatFileItemReaderFactory {

    private final S3Template s3Template;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;


    public <T> MultiResourceItemReader<T> createReader(String prefix, String[] fieldNames, Class<T> type) {
        List<Resource> resources = s3Template.listObjects(bucketName, prefix)
                .stream()
                .map(Resource.class::cast)
                .toList();
        FlatFileItemReader<T> delegate = new FlatFileItemReaderBuilder<T>()
                .name("s3-flatfile-reader")
                .delimited()
                .delimiter(";")
                .names(fieldNames)
                .targetType(type)
                .linesToSkip(1)
                .build();

        MultiResourceItemReader<T> multiReader = new MultiResourceItemReader<>();
        multiReader.setResources(resources.toArray(new Resource[0]));
        multiReader.setDelegate(delegate);

        return multiReader;
    }
}
