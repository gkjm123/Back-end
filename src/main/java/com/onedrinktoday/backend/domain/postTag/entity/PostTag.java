package com.onedrinktoday.backend.domain.postTag.entity;

import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_tag")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class PostTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "posted_tag_id")
  private Long postedTagId;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;

  // Post와 Tag를 받는 생성자 추가
  public PostTag(Post post, Tag tag) {
    this.post = post;
    this.tag = tag;
  }
}