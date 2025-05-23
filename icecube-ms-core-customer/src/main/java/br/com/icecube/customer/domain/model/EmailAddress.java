package br.com.icecube.customer.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAddress {

    @Column(name = "emailAddress")
    private String value;
    private EmailAddress(String value){
        this.value = value;
    }
    public static EmailAddress of(String value) {
        Objects.requireNonNull(value, "the email address cannot be null");
        Assert.isTrue(!value.isBlank(), "the email address cannot be empty");
        var regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        boolean matches = Pattern.compile(regexPattern).matcher(value).matches();
        Assert.isTrue(matches, "invalid email address");

        return new EmailAddress(value);
    }
}
