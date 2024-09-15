package com.onedrinktoday.backend.domain.drink.entity;

import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.global.type.DrinkType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "drink")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Drink {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "region_id")
  private Region region;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  private DrinkType type;

  @Column(name = "degree")
  private Float degree;

  @Column(name = "sweetness")
  private Integer sweetness;

  @Column(name = "cost")
  private Integer cost;

  @Column(name = "description", columnDefinition = "TEXT", nullable = false)
  private String description;

  @Column(name = "image_url")
  private String imageUrl;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
