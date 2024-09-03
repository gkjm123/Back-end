package com.onedrinktoday.backend.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<CustomException> customExceptionHandler(final CustomException e) {
    log.warn("커스텀 에러 발생", e);
    return ResponseEntity.status(e.getStatus()).body(e);
  }
}
