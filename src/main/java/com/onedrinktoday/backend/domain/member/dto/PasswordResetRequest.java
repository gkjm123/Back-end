package com.onedrinktoday.backend.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {

  @NotNull(message = "이메일을 입력해주세요.")
  @Email(message = "올바른 이메일 형식을 입력해주세요.")
  private String email;
}
