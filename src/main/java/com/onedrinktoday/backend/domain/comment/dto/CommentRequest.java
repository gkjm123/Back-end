package com.onedrinktoday.backend.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

  @NotNull(message = "게시글 ID를 입력해주세요.")
  private Long postId;
  @Size(max = 2000)
  @NotBlank(message = "댓글 내용을 입력해주세요.")
  private String content;
  private boolean anonymous;
}