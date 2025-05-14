package br.com.icecube.customer.domain.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LegalName legalName;

    private Document document;

    private EmailAddress emailAddress;

    public Customer(LegalName legalName, Document document, EmailAddress emailAddress) {
        this.legalName = legalName;
        this.document = document;
        this.emailAddress = emailAddress;
    }

    public static Customer create(LegalName legalName, Document document, EmailAddress emailAddress) {
        return new Customer(legalName, document, emailAddress);
    }

    public void changeEmail(final EmailAddress emailAddress){
        this.emailAddress = emailAddress;
    }

}
