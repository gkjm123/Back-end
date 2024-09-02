package com.onedrinktoday.backend.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onedrinktoday.backend.global.type.Drink;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class MemberRequest {

  @Getter
  @Builder
  public static class SignUp {

    private Long regionId;
    private String name;
    private String email;
    private String password;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    private List<Drink> favorDrink;
    private boolean alarmEnabled;

  }

  @Getter
  @Builder
  public static class SignIn {

    private String email;
    private String password;

  }
}
