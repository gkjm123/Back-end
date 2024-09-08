package com.onedrinktoday.backend.domain.post.dto;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
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
  private Drink drink;
  private String type;
  private String title;
  private String content;
  private Float rating;
  private List<String> tag;
}
