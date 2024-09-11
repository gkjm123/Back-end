package com.onedrinktoday.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  CONVERT_ERROR("변환 에러", HttpStatus.BAD_REQUEST),
  DRINK_NOT_FOUND("특산주 정보를 찾을수 없습니다.", HttpStatus.NOT_FOUND),
  DECLARATION_NOT_FOUND("신고 정보를 찾을수 없습니다.", HttpStatus.NOT_FOUND),
  MEMBER_NOT_FOUND("멤버를 찾을수 없습니다.", HttpStatus.NOT_FOUND),
  EMAIL_EXIST("이미 가입된 메일입니다.", HttpStatus.BAD_REQUEST),
  EMAIL_NOT_FOUND("이메일이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  REGISTRATION_NOT_FOUND("특산주 등록 정보를 찾을수 없습니다.", HttpStatus.NOT_FOUND),
  COMMENT_NOT_FOUND("댓글을 찾을수 없습니다.", HttpStatus.NOT_FOUND),
  POST_NOT_FOUND("게시글을 찾을수 없습니다.", HttpStatus.NOT_FOUND),
  TAG_NOT_FOUND("태그를 찾을수 없습니다.", HttpStatus.NOT_FOUND),
  ALREADY_FOLLOWING("이미 팔로우 중인 태그입니다.", HttpStatus.BAD_REQUEST),
  REGION_NOT_FOUND("지역을 찾을수 없습니다.", HttpStatus.NOT_FOUND),
  REGION_EXIST("이미 존재하는 지역명입니다.", HttpStatus.BAD_REQUEST),
  LOGIN_FAIL("이메일, 비밀번호를 확인해 주세요.", HttpStatus.BAD_REQUEST),
  SAME_PASSWORD("새 비밀번호가 기존 비밀번호와 동일합니다.", HttpStatus.BAD_REQUEST),
  TOKEN_EXPIRED("토큰이 유효 기간이 지나서 만료되었습니다.", HttpStatus.UNAUTHORIZED),
  TOKEN_NOT_MATCH("Refresh Token 값이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
  INVALID_REFRESH_TOKEN("리프레시 토큰이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  ACCESS_DENIED("접근이 거부되었습니다.", HttpStatus.FORBIDDEN);

  private final String message;
  private final HttpStatus status;
}
