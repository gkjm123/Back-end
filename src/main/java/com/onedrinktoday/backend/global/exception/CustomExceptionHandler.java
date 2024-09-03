package com.onedrinktoday.backend.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<String> customExceptionHandler(final CustomException e) {

    return ResponseEntity.status(e.getErrorCode().getStatus()).body(e.getErrorCode().getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> processValidationError(MethodArgumentNotValidException e) {

    String errorMessage = e.getBindingResult().getFieldErrors()
        .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0);

    return ResponseEntity.badRequest().body(errorMessage);
  }
}
