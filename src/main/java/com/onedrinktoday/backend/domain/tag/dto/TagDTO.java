package com.onedrinktoday.backend.domain.tag.dto;

import com.onedrinktoday.backend.domain.tag.entity.Tag;
import lombok.Data;

@Data
public class TagDTO {
  private Long tagId;
  private String tagName;

  public static TagDTO from(Tag tag) {
    TagDTO dto = new TagDTO();
    dto.setTagId(tag.getTagId());
    dto.setTagName(tag.getTagName());
    return dto;
  }
}