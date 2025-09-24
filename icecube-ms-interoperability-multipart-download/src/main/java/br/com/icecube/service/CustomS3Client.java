package br.com.icecube.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomS3Client {

    private final S3Template s3Template;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public List<InputStreamResource> downloadInMemory(String bucketName) {
        Assert.notNull(bucketName, "bucketName is required");

        return s3Template.listObjects(bucketName, "2025")
                .stream()
                .flatMap(s3Resource -> {
                    try {
                        InputStream is = s3Resource.getInputStream();
                        InputStreamResource resource = new InputStreamResource(is) {
                            @Override
                            public String getFilename() {
                                return s3Resource.getFilename();
                            }
                        };
                        return Stream.of(resource);
                    } catch (IOException e) {
                        log.error("Failed to read file {}", s3Resource.getFilename(), e);
                        return Stream.empty();
                    }
                })
                .toList();
    }

    public InputStream getFileInputStream(String key) throws IOException {
        return s3Template.download(bucketName, key).getInputStream();
    }

}
