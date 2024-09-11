package com.onedrinktoday.backend.domain.declaration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeclarationRequest {

  @NotBlank(message = "신고할 게시글 링크를 입력해주세요.")
  private String link;

  @NotBlank(message = "신고 사유를 입력해주세요.")
  private String type;

  @Size(min = 10, max = 1000)
  @NotBlank(message = "신고 내용을 입력해주세요.")
  private String content;
}
