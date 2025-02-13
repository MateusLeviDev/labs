package br.com.icecube.customer.domain.service.impl;

import br.com.icecube.customer.api.dto.AddressDTO;
import br.com.icecube.customer.api.dto.CustomerDTO;
import br.com.icecube.customer.domain.model.Address;
import br.com.icecube.customer.domain.model.Customer;
import br.com.icecube.customer.domain.model.Document;
import br.com.icecube.customer.domain.model.LegalName;

import java.util.List;

public class TestDataFactory {

    public static CustomerDTO createCustomerDTO() {
        var address1 = new AddressDTO("Rua A", "123", "Cidade X", "00000-000");
        var address2 = new AddressDTO("Rua B", "456", "Cidade Y", "11111-111");
        return new CustomerDTO("John Doe", "123", List.of(address1, address2));
    }

    public static Customer createCustomerToBeSaved() {
        var address = List.of(Address.builder()
                .id(1L)
                .street("20º Street")
                .city("New York")
                .number("1254")
                .zipcode("NY123").build());

        return Customer.create(LegalName.of("John Doe"), Document.of("123"), address);
    }

    public static Customer createUpdatedCustomer() {
        var updatedAddress = List.of(Address.builder()
                .id(1L)
                .street("Rua C")
                .city("Cidade Z")
                .number("789")
                .zipcode("22222-222").build());

        return Customer.create(LegalName.of("John Doe"), Document.of("123"), updatedAddress);
    }

    public static AddressDTO createUpdatedAddressDTO() {
        return new AddressDTO("Rua C", "789", "Cidade Z", "22222-222");
    }

    public static Address createUpdatedAddress() {
        return Address.builder()
                .id(1L)
                .street("Rua C")
                .city("Cidade Z")
                .number("789")
                .zipcode("22222-222")
                .build();
    }
}
