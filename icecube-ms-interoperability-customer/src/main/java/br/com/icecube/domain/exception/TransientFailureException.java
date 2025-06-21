package br.com.icecube.domain.exception;

import lombok.Value;

@Value
public class TransientFailureException extends RuntimeException {

    String reason;
}
