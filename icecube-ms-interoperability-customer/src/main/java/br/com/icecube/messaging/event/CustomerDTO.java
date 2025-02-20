package br.com.icecube.messaging.event;

import java.time.LocalDate;

public record CustomerDTO(String firstName, String lastName, LocalDate birthDate, String emailAddress, Integer ssn) {
}
