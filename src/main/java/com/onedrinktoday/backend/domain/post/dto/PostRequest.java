package com.onedrinktoday.backend.domain.post.dto;

import com.onedrinktoday.backend.global.type.Type;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
  @Setter
  private Long drinkId;

  private Long memberId;

  private Type type;

  @Setter
  @Size(min = 10, max = 3000)
  private String content;

  @Setter
  private Float rating;

  @Setter
  private List<String> tag;

  private String imageUrl;
}
