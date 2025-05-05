package br.com.icecube.customer.api.dto;

public record CustomerKafkaDTO(String firstName, String birthDate, String emailAddress, Integer ssn) {
}
