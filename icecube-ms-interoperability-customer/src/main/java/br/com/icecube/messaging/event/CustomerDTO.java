package br.com.icecube.messaging.event;

import java.time.LocalDate;

public record CustomerDTO(String firstName, String birthDate, String emailAddress, Integer ssn) {
}
