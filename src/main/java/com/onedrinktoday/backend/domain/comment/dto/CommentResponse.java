package com.onedrinktoday.backend.domain.comment.dto;

import com.onedrinktoday.backend.domain.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

  private Long id;
  private Long memberId;
  private String memberName;
  private Long postId;
  private String content;
  private boolean anonymous;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static CommentResponse from(Comment comment) {
    return CommentResponse.builder()
        .id(comment.getId())
        .memberId(comment.getMember().getId())
        .memberName(comment.getMember().getName())
        .postId(comment.getPost().getId())
        .content(comment.getContent())
        .anonymous(comment.isAnonymous())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }
}