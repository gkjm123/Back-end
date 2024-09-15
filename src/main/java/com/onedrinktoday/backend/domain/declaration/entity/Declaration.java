package com.onedrinktoday.backend.domain.declaration.entity;

import com.onedrinktoday.backend.domain.declaration.dto.DeclarationRequest;
import com.onedrinktoday.backend.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Declaration {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne
  private Member member;

  private String link;
  private String type;
  private String content;

  @Setter
  private Boolean approved;

  @CreationTimestamp
  private LocalDateTime createdAt;

  public static Declaration from(DeclarationRequest request) {
    return Declaration.builder()
        .link(request.getLink())
        .type(request.getType())
        .content(request.getContent())
        .build();
  }
}
