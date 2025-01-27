package br.com.icecube.customer.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDTO<T> {

    private int code;
    private String message;
    private T details;

    @Data
    @Builder
    public static class FieldError {

        private String field;
        private String message;
    }
}
