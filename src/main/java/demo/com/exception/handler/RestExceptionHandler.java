package demo.com.exception.handler;

import demo.com.exception.BadRequestException;
import demo.com.exception.ElementNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice //os demais controllers devem utilizar as flags
public class RestExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DefaultExceptionDetails> handlerBadRequestException(BadRequestException bre) {
        return new ResponseEntity<>(
                DefaultExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .title("Check the documentation")
                        .details(bre.getMessage())
                        .developerMessage(bre.getClass().getName())
                        .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<DefaultExceptionDetails> elementNotFoundException(ElementNotFoundException efe) {
        return new ResponseEntity<>(
                DefaultExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .title("Resource not found")
                        .details(efe.getMessage())
                        .developerMessage(efe.getClass().getName())
                        .build(), HttpStatus.NOT_FOUND);
    }
}
