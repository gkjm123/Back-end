package com.onedrinktoday.backend.domain.member.dto;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.global.type.DrinkType;
import com.onedrinktoday.backend.global.type.Role;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

  private Long id;
  private String placeName;
  private String name;
  private String email;
  private Date birthDate;
  private List<DrinkType> favorDrinkType;
  private Role role;
  private Boolean alarmEnabled;
  private String imageUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  public static MemberResponse from(Member member) {
    return MemberResponse.builder()
        .id(member.getId())
        .placeName(member.getRegion() == null ? null : member.getRegion().getPlaceName())
        .name(member.getName())
        .email(member.getEmail())
        .birthDate(member.getBirthDate())
        .favorDrinkType(member.getFavorDrinkType())
        .role(member.getRole())
        .alarmEnabled(member.getAlarmEnabled())
        .imageUrl(member.getImageUrl())
        .createdAt(member.getCreatedAt())
        .updatedAt(member.getUpdatedAt())
        .deletedAt(member.getDeletedAt())
        .build();
  }
}
