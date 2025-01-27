package br.com.icecube.customer.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document {

    @Column(name = "document")
    private String value;

    private Document(final String value) {
        this.value = value;
    }

    public static Document of(final String value) {
        Objects.requireNonNull(value, "Document cannot be null");
        Assert.isTrue(!value.isBlank(), "Document cannot be empty");

        return new Document(value);
    }
}
