package com.onedrinktoday.backend.domain.declaration.dto;

import com.onedrinktoday.backend.domain.declaration.entity.Declaration;
import com.onedrinktoday.backend.global.type.CancelDeclarationType;
import com.onedrinktoday.backend.global.type.DeclarationType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeclarationResponse {

  private Long id;
  private Long memberId;
  private String memberName;
  private String link;
  private DeclarationType type;
  private String content;
  private Boolean approved;
  private CancelDeclarationType cancelType;
  private LocalDateTime createdAt;

  public static DeclarationResponse from(Declaration declaration) {
    return DeclarationResponse.builder()
        .id(declaration.getId())
        .memberId(declaration.getMember().getId())
        .memberName(declaration.getMember().getName())
        .link(declaration.getLink())
        .type(declaration.getType())
        .content(declaration.getContent())
        .approved(declaration.getApproved())
        .cancelType(declaration.getCancelType())
        .createdAt(declaration.getCreatedAt())
        .build();
  }
}
