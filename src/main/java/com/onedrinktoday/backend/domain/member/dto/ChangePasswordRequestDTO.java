package com.onedrinktoday.backend.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDTO {
  @NotNull(message = "현재 비밀번호를 입력해주세요.")
  private String currentPassword;

  @NotNull(message = "새 비밀번호를 입력해주세요.")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!%*?&])[A-Za-z\\d$@!%*?&]{8,15}",
      message = "비밀번호는 8자 이상 15자 이하로, 소문자, 대문자, 숫자 및 특수문자를 모두 포함해야 합니다.")
  private String newPassword;
}
