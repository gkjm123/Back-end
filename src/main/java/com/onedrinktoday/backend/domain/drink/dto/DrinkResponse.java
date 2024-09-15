package com.onedrinktoday.backend.domain.drink.dto;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.global.type.DrinkType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrinkResponse {
  private Long id;
  private String placeName;
  private String name;
  private DrinkType type;
  private Float degree;
  private Integer sweetness;
  private Integer cost;

  @Setter
  private Float averageRating;
  private String description;
  private String imageUrl;
  private LocalDateTime createdAt;

  public static DrinkResponse from(Drink drink) {
    return DrinkResponse.builder()
        .id(drink.getId())
        .placeName(drink.getRegion().getPlaceName())
        .name(drink.getName())
        .type(drink.getType())
        .degree(drink.getDegree())
        .sweetness(drink.getSweetness())
        .cost(drink.getCost())
        .description(drink.getDescription())
        .imageUrl(drink.getImageUrl())
        .createdAt(drink.getCreatedAt())
        .build();
  }
}
