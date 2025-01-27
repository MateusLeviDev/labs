package br.com.icecube.customer.api.dto;

public record AddressDTO(
        String street,
        String number,
        String city,
        String zipcode
) {
}
