package br.com.icecube.infrastructure.messaging.event;

import java.time.LocalDate;

public record CustomerDTO(
        String legalName,
        String document,
        String emailAddress
) {
}
