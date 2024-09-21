package com.onedrinktoday.backend.domain.declaration.dto;

import com.onedrinktoday.backend.global.type.DeclarationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeclarationRequest {

  @NotBlank(message = "신고할 게시글 링크를 입력해주세요.")
  private String link;

  @NotNull(message = "신고 사유를 선택해주세요.")
  private DeclarationType type;

  @Size(min = 10, max = 1000)
  @NotBlank(message = "신고 내용을 입력해주세요.")
  private String content;
}
