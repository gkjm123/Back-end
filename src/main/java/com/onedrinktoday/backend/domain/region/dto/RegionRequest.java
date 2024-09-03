package com.onedrinktoday.backend.domain.region.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RegionRequest {

  @Size(min = 2, max = 10, message = "지역명을 2~10자 사이로 입력해주세요.")
  private String placeName;

  @NotNull(message = "위도를 입력해주세요")
  private Double latitude;

  @NotNull(message = "경도를 입력해주세요")
  private Double longitude;

}
