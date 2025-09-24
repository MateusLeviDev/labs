package br.com.icecube.batch;

import br.com.icecube.dto.SalesDTO;
import br.com.icecube.service.CustomS3Client;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.InputStreamResource;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class S3SalesItemReader implements ItemStreamReader<SalesDTO> {

    private final CustomS3Client s3Client;
    private final List<String> keys;
    private Iterator<String> keyIterator;
    private FlatFileItemReader<SalesDTO> currentReader;
    private ExecutionContext executionContext;

    public S3SalesItemReader(CustomS3Client s3Client, List<String> keys) {
        this.s3Client = s3Client;
        this.keys = keys;
        this.keyIterator = keys.iterator();
    }

    @Override
    public SalesDTO read() throws Exception {
        if (currentReader == null && keyIterator.hasNext()) {
            openNextReader();
        }

        if (Objects.isNull(currentReader)) {
            return null;
        }

        SalesDTO dto = currentReader.read();

        if (Objects.isNull(dto)) {
            if (keyIterator.hasNext()) {
                openNextReader();
                return read();
            } else {
                return null;
            }
        }

        return dto;
    }


    private void openNextReader() throws Exception {
        String key = keyIterator.next();

        currentReader = new FlatFileItemReaderBuilder<SalesDTO>()
                .name("s3FileReader-" + key)
                .resource(new InputStreamResource(s3Client.getFileInputStream(key)) {
                    @Override
                    public String getFilename() {
                        return key;
                    }
                })
                .delimited()
                .delimiter(";")
                .names("saleId", "productId", "customerId", "saleDate", "saleAmount", "location", "country")
                .targetType(SalesDTO.class)
                .linesToSkip(1)
                .saveState(true)
                .build();

        currentReader.afterPropertiesSet();
        currentReader.open(executionContext);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (Objects.nonNull(currentReader)) {
            currentReader.update(executionContext);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        if (Objects.nonNull(currentReader)) {
            currentReader.close();
        }
    }
}
