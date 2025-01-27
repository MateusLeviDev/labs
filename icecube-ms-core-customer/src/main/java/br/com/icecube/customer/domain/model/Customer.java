package br.com.icecube.customer.domain.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @JsonManagedReference
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> address;

    public Customer(LegalName legalName, Document document, List<Address> address) {
        this.legalName = legalName;
        this.document = document;
        this.address = address;
    }

    public static Customer create(LegalName legalName, Document document, List<Address> address) {
        return new Customer(legalName, document, address);
    }

    public void updateAddress(final Long addressId, final Address updatedAddress) {
        this.address.stream()
                .filter(existingAddress -> existingAddress.getId().equals(addressId))
                .findFirst()
                .ifPresent(existingAddress -> {
                    existingAddress.setStreet(updatedAddress.getStreet());
                    existingAddress.setNumber(updatedAddress.getNumber());
                    existingAddress.setCity(updatedAddress.getCity());
                    existingAddress.setZipcode(updatedAddress.getZipcode());
                });
    }

}
