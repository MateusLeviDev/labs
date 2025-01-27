package br.com.icecube.customer.api.controller.exception;

import br.com.icecube.customer.api.dto.ExceptionDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String ENTITY_NOT_FOUND = "Entity not found";
    public static final String VALIDATION_ERROR = "validation error";
    public static final String INVALID_INPUT = "Invalid input";
    public static final String BAD_REQUEST = "Bad request";
    public static final String ILLEGAL_ARGUMENT = "Illegal argument";
    public static final String DATA_INTEGRITY_VIOLATION = "Data integrity violation";
    public static final String METHOD_ARGUMENT_INVALID = "Method argument invalid";

    public static final String INTERNAL_SERVER_ERROR = "Internal server error";

    private static final String PACKAGE_NAME_PREFIX = "(br.com.icecube.+\\.)";

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO<Object>> notFoundException(final EntityNotFoundException e) {
        log.error(ENTITY_NOT_FOUND, e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionDTO.builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message(ENTITY_NOT_FOUND)
                        .details(removePackageName(e.getMessage())).build());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDTO<Object>> badRequestException(final BadRequestException bre) {
        log.error(BAD_REQUEST, bre);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDTO.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(BAD_REQUEST)
                        .details(removePackageName(bre.getMessage())).build());
    }

    private String removePackageName(final String input) {
        final Pattern pattern = Pattern.compile(PACKAGE_NAME_PREFIX, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");
    }
}
