package com.onedrinktoday.backend.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onedrinktoday.backend.global.type.DrinkType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class MemberRequest {

  @Getter
  @Builder
  public static class SignUp {

    @NotNull(message = "지역 ID를 입력해주세요.")
    private Long regionId;

    @Pattern(regexp = "^[a-zA-Z0-9가-힣_-]{2,10}$", message = "이름을 2~10글자 사이로 입력해주세요.")
    private String name;

    @Email(message = "메일을 확인해주세요.")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!%*?&])[A-Za-z\\d$@!%*?&]{8,15}", message = "비밀번호는 8자 이상 15자 이하로, 소문자, 대문자, 숫자 및 특수문자를 모두 포함해야 합니다.")
    private String password;

    @Past(message = "생일을 오늘보다 과거일자로 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    private List<DrinkType> favorDrinkType;
    private boolean alarmEnabled;

    public SignUp(Long regionId, String name, String email, String password, Date birthDate, List<DrinkType> favorDrinkType, boolean alarmEnabled) {
      this.regionId = regionId;
      this.name = name;
      this.email = email;
      this.password = password;
      this.birthDate = birthDate;
      this.favorDrinkType = favorDrinkType;
      this.alarmEnabled = alarmEnabled;
    }
  }

  @Getter
  @Builder
  public static class SignIn {

    @NotNull(message = "이메일을 입력해주세요.")
    private String email;

    @NotNull(message = "비밀번호를 입력해주세요.")
    private String password;

    public SignIn(String email, String password) {
      this.email = email;
      this.password = password;
    }
  }

  @Getter
  @Builder
  public static class UpdateInfo {

    private Long regionId;

    @Pattern(regexp = "^[a-zA-Z0-9가-힣_-]{2,10}$", message = "이름을 2~10글자 사이로 입력해주세요.")
    private String name;

    private List<DrinkType> favorDrinkType;
    private boolean alarmEnabled;
    private String imageUrl;

    public UpdateInfo(Long regionId, String name, List<DrinkType> favorDrinkType, boolean alarmEnabled, String imageUrl) {
      this.regionId = regionId;
      this.name = name;
      this.favorDrinkType = favorDrinkType;
      this.alarmEnabled = alarmEnabled;
      this.imageUrl = imageUrl;
    }
  }
}
