package com.onedrinktoday.backend.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final int status;
  private final ErrorCode errorCode;
  private final String message;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.status = errorCode.getStatus();
    this.errorCode = errorCode;
    this.message = errorCode.getMessage();
  }
}
