package br.com.icecube.messaging.event;

import java.time.LocalDate;

public record CustomerDTO(
        String legalName,
        String document,
        String emailAddress
) {
}
