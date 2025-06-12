package com.example.demo.handlers;

import com.example.demo.exceptions.GenericException;
import com.example.demo.exceptions.ObjectValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({ObjectValidationException.class})
  public ResponseEntity<ExceptionRepresentation> handleException(ObjectValidationException exception) {
    ExceptionRepresentation representation = ExceptionRepresentation.builder()
        .errorMessage("Object not valid exception has occurred")
        .errorSource(exception.getViolationSource())
        .validationErrors(exception.getViolations())
        .build();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(representation);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ExceptionRepresentation> handleException(EntityNotFoundException exception) {
    ExceptionRepresentation representation = ExceptionRepresentation.builder()
        .errorMessage(exception.getMessage())
        .build();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(representation);
  }

  @ExceptionHandler(GenericException.class)
  public ResponseEntity<ExceptionRepresentation> handleException(GenericException exception) {
    ExceptionRepresentation representation = ExceptionRepresentation.builder()
            .errorMessage(exception.getErrorMsg())
            .build();
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(representation);
  }

}
