package com.onedrinktoday.backend.domain.member.entity;

import com.onedrinktoday.backend.domain.member.dto.MemberRequest;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.global.type.Drink;
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
import java.sql.Timestamp;
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
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "region_id")
  private Region region;

  private String name;
  private String email;
  private String password;
  private Date birthDate;

  @Convert(converter = DrinkListConverter.class)
  private List<Drink> favorDrink;

  @Enumerated(EnumType.STRING)
  private Role role;

  private boolean alarmEnabled;

  private String imageUrl;

  @CreationTimestamp
  private Timestamp createdAt;

  @UpdateTimestamp
  private Timestamp updatedAt;

  private Timestamp deletedAt;

  public static Member from(MemberRequest.SignUp request) {
    return Member.builder()
        .name(request.getName())
        .email(request.getEmail())
        .birthDate(request.getBirthDate())
        .favorDrink(request.getFavorDrink())
        .role(Role.USER)
        .alarmEnabled(request.isAlarmEnabled())
        .build();
  }
}
