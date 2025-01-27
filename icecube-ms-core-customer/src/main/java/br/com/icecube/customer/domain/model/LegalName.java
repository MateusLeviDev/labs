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
public class LegalName {

    @Column(name = "legalName")
    private String value;

    private LegalName(final String value) {
        this.value = value;
    }

    public static LegalName of(final String value) {
        Objects.requireNonNull(value, "Legal name cannot be null");
        Assert.isTrue(!value.isBlank(), "Legal name cannot be empty");

        return new LegalName(value);
    }
}
