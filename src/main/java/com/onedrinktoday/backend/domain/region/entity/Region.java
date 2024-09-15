package com.onedrinktoday.backend.domain.region.entity;

import com.onedrinktoday.backend.domain.region.dto.RegionRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {

  @Setter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  private String placeName;

  private Double latitude;
  private Double longitude;

  @CreationTimestamp
  private LocalDateTime createdAt;

  public static Region from(RegionRequest request) {
    return Region.builder()
        .placeName(request.getPlaceName())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .build();
  }
}
