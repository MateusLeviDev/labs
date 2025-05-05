package br.com.icecube.exception;

import lombok.Value;

@Value
public class TransientFailureException extends RuntimeException {

    String reason;
}
