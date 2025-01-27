package br.com.icecube.customer.api.dto;

import java.util.List;

public record CustomerDTO(
        String legalName,
        String document,
        List<AddressDTO> address
) {
}
