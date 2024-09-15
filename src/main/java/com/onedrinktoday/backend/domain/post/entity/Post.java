package com.onedrinktoday.backend.domain.post.entity;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.global.type.Type;
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
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "post")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @Setter
  @ManyToOne
  @JoinColumn(name = "drink_id")
  private Drink drink;

  @Enumerated(EnumType.STRING)
  private Type type;

  @Setter
  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Setter
  @Column(name = "rating", nullable = true)
  private Float rating;

  @Column(name = "image_url")
  private String imageUrl;

  @Setter
  @Column(name = "view_count", nullable = true)
  private Integer viewCount;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;
}
