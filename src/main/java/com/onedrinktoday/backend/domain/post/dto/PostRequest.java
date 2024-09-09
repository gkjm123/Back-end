package com.onedrinktoday.backend.domain.post.dto;

import java.util.List;
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
public class PostRequest {
  private Long drinkId;
  private Long memberId;
  private String type;
  private String content;
  private Float rating;
  private List<String> tag;

  // 새 음료 입력
  private String drinkName;
  private String description;
  private Integer degree;
  private Integer sweetness;
  private Integer cost;
  private String imageUrl;
  private Long regionId;
  private com.onedrinktoday.backend.global.type.Drink drinkType;
}
