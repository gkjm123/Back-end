package com.onedrinktoday.backend.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.tag.dto.TagDTO;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import com.onedrinktoday.backend.global.type.PostType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
  private Long id;
  private Long memberId;
  private String memberName;
  @Setter
  private DrinkResponse drink;
  private PostType type;
  @Setter
  private String content;
  @Setter
  private Float rating;
  @Setter
  private List<TagDTO> tags;
  private String imageUrl;
  private Integer viewCount;
  private Integer likeCount;
  @Setter
  @JsonProperty("isLiked")
  private boolean isLiked;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static PostResponse of(Post post, List<Tag> tags, boolean isLiked) {
    String memberName = post.getMember() != null ? post.getMember().getName() : "탈퇴한 사용자";
    Long memberId = post.getMember() != null ? post.getMember().getId() : null;
    String imageUrl = post.getImageUrl() != null ? post.getImageUrl() : post.getDrink().getImageUrl();

    return PostResponse.builder()
        .id(post.getId())
        .memberId(memberId)
        .memberName(memberName)
        .drink(DrinkResponse.from(post.getDrink()))
        .type(post.getType())
        .content(post.getContent())
        .rating(post.getRating())
        .tags(tags.stream().map(TagDTO::from).collect(Collectors.toList()))
        .imageUrl(imageUrl)
        .viewCount(post.getViewCount())
        .likeCount(post.getLikeCount())
        .isLiked(isLiked)
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build();
  }

  // post 엔티티 변환
  public static PostResponse from(Post post) {
    return PostResponse.builder()
        .id(post.getId())
        .memberId(post.getMember().getId())
        .memberName(post.getMember().getName())
        .drink(DrinkResponse.from(post.getDrink()))
        .type(post.getType())
        .content(post.getContent())
        .rating(post.getRating())
        .imageUrl(post.getImageUrl())
        .viewCount(post.getViewCount())
        .likeCount(post.getLikeCount())
        .createdAt(post.getCreatedAt())
        .drink(DrinkResponse.from(post.getDrink()))
        .build();
  }
}
