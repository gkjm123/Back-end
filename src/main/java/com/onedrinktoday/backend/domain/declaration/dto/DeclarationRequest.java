package com.onedrinktoday.backend.domain.declaration.dto;

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

  private String link;
  private String type;
  private String content;
}
