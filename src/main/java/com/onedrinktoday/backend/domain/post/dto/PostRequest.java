package com.onedrinktoday.backend.domain.post.dto;

import com.onedrinktoday.backend.global.type.Type;
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
  private Type type;
  private String content;
  private Float rating;
  private List<String> tag;

}
