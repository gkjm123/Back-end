package com.onedrinktoday.backend.domain.registration.dto;

import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.global.type.DrinkType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

  private Long id;
  private Long memberId;
  private String memberName;
  private String placeName;
  private String drinkName;
  private DrinkType type;
  private Float degree;
  private Integer sweetness;
  private Integer cost;
  private String description;
  private String imageUrl;
  private Boolean approved;
  private LocalDateTime createdAt;

  public static RegistrationResponse from(Registration registration) {
    Long memberId = registration.getMember() != null ? registration.getMember().getId() : null;
    String memberName = memberId != null ? registration.getMember().getName() : "탈퇴한 사용자";

    return RegistrationResponse.builder()
        .id(registration.getId())
        .memberId(memberId)
        .memberName(memberName)
        .placeName(registration.getRegion().getPlaceName())
        .drinkName(registration.getDrinkName())
        .type(registration.getType())
        .degree(registration.getDegree())
        .sweetness(registration.getSweetness())
        .cost(registration.getCost())
        .description(registration.getDescription())
        .imageUrl(registration.getImageUrl())
        .approved(registration.getApproved())
        .createdAt(registration.getCreatedAt())
        .build();
  }
}
