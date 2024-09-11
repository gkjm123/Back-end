package com.onedrinktoday.backend.domain.tagFollow.dto;

import com.onedrinktoday.backend.domain.tagFollow.entity.TagFollow;
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
public class TagFollowResponse {

  private Long id;
  private Long memberId;
  private String memberName;
  private Long tagId;
  private String tagName;

  public static TagFollowResponse from(TagFollow tagFollow) {
    return TagFollowResponse.builder()
        .id(tagFollow.getId())
        .memberId(tagFollow.getMember().getId())
        .memberName(tagFollow.getMember().getName())
        .tagId(tagFollow.getTag().getTagId())
        .tagName(tagFollow.getTag().getTagName())
        .build();
  }
}