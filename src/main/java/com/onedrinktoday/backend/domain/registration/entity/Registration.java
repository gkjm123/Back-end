package com.onedrinktoday.backend.domain.registration.entity;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.registration.dto.RegistrationRequest;
import com.onedrinktoday.backend.global.type.DrinkType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne
  private Member member;

  @Setter
  @ManyToOne
  private Region region;

  private String drinkName;
  private DrinkType type;
  private Float degree;
  private Integer sweetness;
  private Integer cost;
  private String description;
  private String imageUrl;

  @Setter
  private Boolean approved;

  @CreationTimestamp
  private LocalDateTime createdAt;

  public static Registration from(RegistrationRequest request) {
    return Registration.builder()
        .drinkName(request.getDrinkName())
        .type(request.getType())
        .degree(request.getDegree())
        .sweetness(request.getSweetness())
        .cost(request.getCost())
        .description(request.getDescription())
        .imageUrl(request.getImageUrl())
        .build();
  }
}
