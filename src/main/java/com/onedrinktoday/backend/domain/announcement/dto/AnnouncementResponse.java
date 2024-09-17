package com.onedrinktoday.backend.domain.announcement.dto;

import com.onedrinktoday.backend.domain.announcement.entity.Announcement;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {

  private Long id;
  private Long memberId;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static AnnouncementResponse from(Announcement announcement) {
    return AnnouncementResponse.builder()
        .id(announcement.getId())
        .memberId(announcement.getMember().getId())
        .title(announcement.getTitle())
        .content(announcement.getContent())
        .createdAt(announcement.getCreatedAt())
        .updatedAt(announcement.getUpdatedAt())
        .build();
  }
}
