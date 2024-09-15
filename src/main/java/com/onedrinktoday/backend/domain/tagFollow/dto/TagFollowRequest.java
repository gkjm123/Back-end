package com.onedrinktoday.backend.domain.tagFollow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class TagFollowRequest {

  @NotBlank
  private Long tagId;
}