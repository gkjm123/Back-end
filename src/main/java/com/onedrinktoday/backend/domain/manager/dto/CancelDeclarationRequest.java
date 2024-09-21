package com.onedrinktoday.backend.domain.manager.dto;

import com.onedrinktoday.backend.global.type.CancelDeclarationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelDeclarationRequest {

  private CancelDeclarationType type;
}
