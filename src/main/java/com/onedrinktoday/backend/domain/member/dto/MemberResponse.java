package com.onedrinktoday.backend.domain.member.dto;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.global.type.Drink;
import com.onedrinktoday.backend.global.type.Role;
import java.sql.Timestamp;
import java.util.Date;
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
public class MemberResponse {

  private Long id;
  private String placeName;
  private String name;
  private String email;
  private Date birthDate;
  private List<Drink> favorDrink;
  private Role role;
  private boolean alarmEnabled;
  private String imageUrl;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Timestamp deletedAt;

  public static MemberResponse from(Member member) {
    return MemberResponse.builder()
        .id(member.getId())
        .placeName(member.getRegion().getPlaceName())
        .name(member.getName())
        .email(member.getEmail())
        .birthDate(member.getBirthDate())
        .favorDrink(member.getFavorDrink())
        .role(member.getRole())
        .alarmEnabled(member.isAlarmEnabled())
        .imageUrl(member.getImageUrl())
        .createdAt(member.getCreatedAt())
        .updatedAt(member.getUpdatedAt())
        .deletedAt(member.getDeletedAt())
        .build();
  }
}
