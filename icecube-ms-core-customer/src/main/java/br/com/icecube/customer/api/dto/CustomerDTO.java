package br.com.icecube.customer.api.dto;

public record CustomerDTO(
        String legalName,
        String document,
        String emailAddress
) {
}
