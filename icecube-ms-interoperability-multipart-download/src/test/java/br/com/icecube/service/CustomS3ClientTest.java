package br.com.icecube.service;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class CustomS3ClientTest {

    private S3Template s3Template;
    private CustomS3Client customS3Client;

    @BeforeEach
    void setUp() {
        s3Template = mock(S3Template.class);
        customS3Client = new CustomS3Client(s3Template);
    }

    @Test
    void testDownloadInMemory_returnsResources() throws IOException {
        S3Resource file1 = mock(S3Resource.class);
        when(file1.getFilename()).thenReturn("file1.csv");
        when(file1.getInputStream()).thenReturn(new ByteArrayInputStream("conteudo1".getBytes()));

        S3Resource file2 = mock(S3Resource.class);
        when(file2.getFilename()).thenReturn("file2.csv");
        when(file2.getInputStream()).thenReturn(new ByteArrayInputStream("conteudo2".getBytes()));

        when(s3Template.listObjects("meu-bucket", "2025")).thenReturn(List.of(file1, file2));

        List<InputStreamResource> result = customS3Client.downloadInMemory("meu-bucket");

        assertEquals(2, result.size());
        assertEquals("file1.csv", result.get(0).getFilename());
        assertEquals("file2.csv", result.get(1).getFilename());
    }

    @Test
    void testDownloadInMemory_handlesIOException() throws IOException {
        S3Resource badFile = mock(S3Resource.class);
        when(badFile.getFilename()).thenReturn("bad.csv");
        when(badFile.getInputStream()).thenThrow(new IOException("Erro ao ler"));

        when(s3Template.listObjects("meu-bucket", "2025")).thenReturn(List.of(badFile));

        List<InputStreamResource> result = customS3Client.downloadInMemory("meu-bucket");

        assertTrue(result.isEmpty());
    }

}