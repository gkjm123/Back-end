package com.onedrinktoday.backend.domain.member.entity;

import com.onedrinktoday.backend.domain.member.dto.MemberRequest;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.global.type.DrinkType;
import com.onedrinktoday.backend.global.type.Role;
import com.onedrinktoday.backend.global.util.DrinkListConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Member {

  @Setter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne
  @JoinColumn(name = "region_id")
  private Region region;

  @Setter
  private String name;

  @Setter
  private String email;

  @Setter
  private String password;
  private Date birthDate;

  @Setter
  @Convert(converter = DrinkListConverter.class)
  private List<DrinkType> favorDrinkType;

  @Setter
  @Enumerated(EnumType.STRING)
  private Role role;

  @Setter
  private Boolean alarmEnabled;

  @Setter
  private String imageUrl;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;

  @Setter
  private String refreshToken;

  public static Member from(MemberRequest.SignUp request) {
    return Member.builder()
        .name(request.getName())
        .email(request.getEmail())
        .birthDate(request.getBirthDate())
        .favorDrinkType(request.getFavorDrinkType())
        .role(Role.USER)
        .alarmEnabled(request.getAlarmEnabled())
        .build();
  }
}
