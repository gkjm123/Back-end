package com.onedrinktoday.backend.domain.post.service;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.post.dto.PostRequest;
import com.onedrinktoday.backend.domain.post.dto.PostResponse;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.domain.postTag.entity.PostTag;
import com.onedrinktoday.backend.domain.postTag.repository.PostTagRepository;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import com.onedrinktoday.backend.domain.tag.repository.TagRepository;
import com.onedrinktoday.backend.global.type.Type;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final DrinkRepository drinkRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;
  private final RegionRepository regionRepository;

  // 게시글 생성 및 저장
  public PostResponse createPost(Long memberId, PostRequest postRequest) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

    Drink drink;

    // 이미 등록된 특산주 사용
    if (postRequest.getDrinkId() != null) {
      drink = drinkRepository.findById(postRequest.getDrinkId())
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 특산주입니다."));
    }

    // 새 특산주 등록 시
    else {
      if (postRequest.getDrinkName() == null || postRequest.getRegionId() == null) {
        throw new IllegalArgumentException("음료 이름과 지역 ID는 필수입니다.");
      }

      // 지역 유효성 검사
      Region region = regionRepository.findById(postRequest.getRegionId())
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 지역입니다."));

      drink = Drink.builder()
          .name(postRequest.getDrinkName())
          .description(postRequest.getDescription())
          .degree(postRequest.getDegree())
          .sweetness(postRequest.getSweetness())
          .cost(postRequest.getCost())
          .imageUrl(postRequest.getImageUrl())
          .drink(postRequest.getDrinkType())
          .region(region)
          .build();

      drink = drinkRepository.save(drink);
    }

    // 게시글 엔티티 생성
    Post post = Post.builder()
        .member(member)
        .drink(drink)
        .type(Type.valueOf(postRequest.getType()))
        .title(postRequest.getTitle())
        .content(postRequest.getContent())
        .rating(postRequest.getRating())
        .viewCount(0)  // 초기 조회수
        .build();

    // 게시글 저장
    post = postRepository.save(post);

    // 태그 저장 및 PostTag 연결
    List<Tag> tags = saveTags(postRequest.getTag(), post);

    return PostResponse.from(post, tags);
  }

  // 태그 저장
  private List<Tag> saveTags(List<String> tagNames, Post post) {
    return tagNames.stream().map(tagName -> {
      Tag tag = tagRepository.findByTagName(tagName)
          .orElseGet(() -> {
            Tag newTag = new Tag();
            newTag.setTagName(tagName);
            return tagRepository.save(newTag);
          });
      PostTag postTag = new PostTag();
      postTag.setPost(post);
      postTag.setTag(tag);
      postTagRepository.save(postTag);

      return tag;
    }).collect(Collectors.toList());
  }

  // 전체 게시글 조회
  public List<PostResponse> getAllPosts() {
    List<Post> Posts = postRepository.findAll();
    return Posts.stream()
        .map(PostResponse::from)
        .collect(Collectors.toList());
  }

  // 특정 게시글 조회
  public PostResponse getPostById(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 게시글 ID입니다."));
    return PostResponse.from(post);
  }

  // 게시글 삭제
  public void deletePostById(Long postId) {
    if(!postRepository.existsById(postId)) {
      throw new IllegalArgumentException("유효하지 않은 게시글 ID입니다.");
    }
    postRepository.deleteById(postId);
  }
}
