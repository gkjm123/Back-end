package com.onedrinktoday.backend.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeclarationType {
  ILLEGAL_INFORMATION("불법, 사기, 위법 행위 관련 사유"),
  PERSONAL_INFORMATION_EXPOSURE("개인정보 노출 사유"),
  OBSCENE("음란성/선정성 사유"),
  PROFANE_LANGUAGE("비속어 사용 사유"),
  SPAMMING("반복적인 게시글 도배 사유"),
  COPYRIGHT_INFRINGEMENT("저작권자 허가 없이 게시글 게시한 사유"),
  OTHER("기타 신고 사유");

  private final String message;
}
