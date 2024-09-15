package com.onedrinktoday.backend.domain.post.dto;

import com.onedrinktoday.backend.global.type.Type;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
  private Long drinkId;
  private Long memberId;
  private Type type;
  @Size(min = 10, max = 3000)
  private String content;
  private Float rating;
  private List<String> tag;

}
