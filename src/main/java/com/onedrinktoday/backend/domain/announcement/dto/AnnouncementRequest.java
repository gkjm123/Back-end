package com.onedrinktoday.backend.domain.announcement.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequest {

  @Size(min = 2, max = 50)
  private String title;

  @Size(min = 10, max = 3000)
  private String content;
}
