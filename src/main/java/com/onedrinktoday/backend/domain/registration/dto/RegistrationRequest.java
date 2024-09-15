package com.onedrinktoday.backend.domain.registration.dto;

import com.onedrinktoday.backend.global.type.DrinkType;
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
  @Size(min = 2, max = 50)
  private String drinkName;
  private DrinkType type;
  private Float degree;
  private Integer sweetness;
  private Integer cost;
  @Size(min = 10, max = 500)
  private String description;
}
