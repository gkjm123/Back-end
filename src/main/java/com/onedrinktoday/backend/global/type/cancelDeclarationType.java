package com.onedrinktoday.backend.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum cancelDeclarationType {
  POST_DELETED_BY_USER("신고하신 회원의 게시글을 삭제하여 게시글이 존재하지 않습니다."),
  NOT_RELEVANT("신고 내용이 게시글과 관련이 없습니다."),
  MISUNDERSTANDING("신고자가 게시글을 오해하거나 잘못 해석하였습니다."),
  NOT_ENOUGH_DETAILS("신고 사유에 대한 구체적인 정보나 설명이 부족합니다."),
  DUPLICATE_REPORT("신고자의 의미없는 반복된 신고입니다.");

  private final String message;
}