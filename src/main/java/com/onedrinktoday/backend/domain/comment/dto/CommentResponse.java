package com.onedrinktoday.backend.domain.comment.dto;

import com.onedrinktoday.backend.domain.comment.entity.Comment;
import java.sql.Timestamp;
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
public class CommentResponse {

  private Long id;
  private Long memberId;
  private String memberName;
  private Long postId;
  private String content;
  private boolean anonymous;
  private Timestamp createdAt;
  private Timestamp updatedAt;

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