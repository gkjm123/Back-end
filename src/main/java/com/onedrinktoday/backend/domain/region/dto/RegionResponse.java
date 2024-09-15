package com.onedrinktoday.backend.domain.region.dto;

import com.onedrinktoday.backend.domain.region.entity.Region;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionResponse {

  private Long id;
  private String placeName;
  private Double latitude;
  private Double longitude;
  private LocalDateTime createdAt;

  public static RegionResponse from(Region region) {
    return RegionResponse.builder()
        .id(region.getId())
        .placeName(region.getPlaceName())
        .latitude(region.getLatitude())
        .longitude(region.getLongitude())
        .createdAt(region.getCreatedAt())
        .build();
  }
}
