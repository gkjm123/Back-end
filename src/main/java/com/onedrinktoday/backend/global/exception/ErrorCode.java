package com.onedrinktoday.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  CONVERT_ERROR("변환 에러", HttpStatus.BAD_REQUEST),
  EMAIL_EXIST("이미 가입된 메일입니다.", HttpStatus.BAD_REQUEST),
  EMAIL_NOT_FOUND("이메일이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
  REGION_NOT_FOUND("지역을 찾을수 없습니다.", HttpStatus.BAD_REQUEST),
  REGION_EXIST("이미 존재하는 지역명입니다.", HttpStatus.BAD_REQUEST),
  LOGIN_FAIL("이메일, 비밀번호를 확인해 주세요.", HttpStatus.BAD_REQUEST);

  private final String message;
  private final HttpStatus status;
}
