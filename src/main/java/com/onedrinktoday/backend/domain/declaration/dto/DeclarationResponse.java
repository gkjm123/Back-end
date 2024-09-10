package com.onedrinktoday.backend.domain.declaration.dto;

import com.onedrinktoday.backend.domain.declaration.entity.Declaration;
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
public class DeclarationResponse {

  private Long id;
  private Long memberId;
  private String memberName;
  private String link;
  private String type;
  private String content;
  private Boolean approved;
  private Timestamp createdAt;

  public static DeclarationResponse from(Declaration declaration) {
    return DeclarationResponse.builder()
        .id(declaration.getId())
        .memberId(declaration.getMember().getId())
        .memberName(declaration.getMember().getName())
        .link(declaration.getLink())
        .type(declaration.getType())
        .content(declaration.getContent())
        .approved(declaration.getApproved())
        .createdAt(declaration.getCreatedAt())
        .build();
  }
}
