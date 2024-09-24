package com.onedrinktoday.backend.domain.registration.dto;

import com.onedrinktoday.backend.global.type.DrinkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

  private Long regionId;
  @Size(min = 2, max = 50, message = "특산주 이름을 2~50자 사이로 입력해주세요")
  private String drinkName;
  private DrinkType type;
  private Float degree;
  private Integer sweetness;
  private Integer cost;
  @Size(min = 10, max = 500, message = "특산주 설명을 10~500자 사이로 입력해주세요")
  private String description;
  @NotBlank(message = "특산주 이미지를 업로드해주세요")
  private String imageUrl;
}
