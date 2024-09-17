package com.onedrinktoday.backend.domain.comment.entity;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.post.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "comment")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE comment SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Setter
  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Setter
  @Column(name = "anonymous")
  private boolean anonymous;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;
}
