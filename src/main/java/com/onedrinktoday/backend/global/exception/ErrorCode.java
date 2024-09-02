package com.onedrinktoday.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  CONVERT_ERROR("변환 에러", 400),
  EMAIL_EXIST("이미 가입된 메일입니다.", 400),
  REGION_NOT_FOUND("지역을 찾을수 없습니다.", 400),
  REGION_EXIST("이미 존재하는 지역명입니다.", 400),
  LOGIN_FAIL("이메일, 비밀번호를 확인해 주세요.", 400);

  private final String message;
  private final int status;
}
