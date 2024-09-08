package com.onedrinktoday.backend.domain.drink.dto;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import java.sql.Timestamp;
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
public class DrinkDTO {
  private Long id;
  private String placeName;
  private String name;
  private String drinkType;
  private Integer degree;
  private Integer sweetness;
  private Integer cost;
  private String description;
  private String imageUrl;
  private Timestamp createdAt;

  public static DrinkDTO from(Drink drink) {
    return DrinkDTO.builder()
        .id(drink.getId())
        .placeName(drink.getRegion().getPlaceName())
        .name(drink.getName())
        .drinkType(drink.getDrink().name())
        .degree(drink.getDegree())
        .sweetness(drink.getSweetness())
        .cost(drink.getCost())
        .description(drink.getDescription())
        .imageUrl(drink.getImageUrl())
        .createdAt(drink.getCreatedAt())
        .build();
  }
}
