package com.example.demo.validators;

import com.example.demo.exceptions.ObjectValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ObjectsValidator<T> {

  private final Validator validator;

  public void validate(T objectToValidate) {
    Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);

    if (!violations.isEmpty()) {
      Set<String> errorMessages = violations.stream()
              .map(ConstraintViolation::getMessage)
              .collect(Collectors.toSet());
      throw new ObjectValidationException(errorMessages, objectToValidate.getClass().getName());
    }
  }
}
