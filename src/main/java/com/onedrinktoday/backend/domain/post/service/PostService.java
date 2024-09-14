package com.onedrinktoday.backend.domain.post.service;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.domain.notification.service.NotificationService;
import com.onedrinktoday.backend.domain.post.dto.PostRequest;
import com.onedrinktoday.backend.domain.post.dto.PostResponse;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.domain.postTag.entity.PostTag;
import com.onedrinktoday.backend.domain.postTag.repository.PostTagRepository;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import com.onedrinktoday.backend.domain.tag.repository.TagRepository;
import com.onedrinktoday.backend.global.cache.CacheService;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import com.onedrinktoday.backend.global.type.Role;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostTagRepository postTagRepository;
  private final TagRepository tagRepository;
  private final MemberRepository memberRepository;
  private final DrinkRepository drinkRepository;
  private final MemberService memberService;
  private final CacheManager cacheManager;
  private final CacheService cacheService;
  private final NotificationService notificationService;

  // 게시글 생성 및 저장
  @CacheEvict(key = "#postRequest.drinkId", value = "avg-rating")
  public PostResponse createPost(PostRequest postRequest) {

    Member member = memberService.getMember();

    Drink drink = drinkRepository.findById(postRequest.getDrinkId())
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 특산주입니다."));

    // 게시글 엔티티 생성
    Post post = Post.builder()
        .member(member)
        .drink(drink)
        .type(postRequest.getType())
        .content(postRequest.getContent())
        .rating(postRequest.getRating())
        .viewCount(0)  // 초기 조회수
        .build();

    // 게시글 저장
    post = postRepository.save(post);

    // 태그 저장 및 PostTag 연결
    List<Tag> tags = saveTags(postRequest.getTag(), post);

    notificationService.tagFollowPostNotification(post.getId(), tags);
    return PostResponse.of(post, tags);
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

      postTagRepository.findByPostAndTag(post, tag)
          .orElseGet(() -> {
            PostTag newPostTag = new PostTag();
            newPostTag.setPost(post);
            newPostTag.setTag(tag);
            return postTagRepository.save(newPostTag);
          });

      return tag;
    }).collect(Collectors.toList());
  }

  // 전체 게시글 조회
  public Page<PostResponse> getAllPosts(Pageable pageable, String sortBy) {
    Page<Post> posts;

    if("viewCount".equals(sortBy)) {
      posts = postRepository.findAllByOrderByViewCountDesc(pageable); // 조회수 정렬
    } else {
      posts = postRepository.findAllByOrderByCreatedAtDesc(pageable); // 최신순 정렬
    }

    // posts가 null이 아닌지 확인
    if (posts == null || posts.isEmpty()) {
      return Page.empty(pageable);  // 빈 페이지 반환
    }

    return posts.map(post -> {
      List<Tag> tags = postTagRepository.findTagsByPostId(post.getId());
      return PostResponse.of(post, tags);
    });
  }

  // 특정 게시글 조회
  public PostResponse getPostById(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 게시글 ID입니다."));

    // viewCount 증가
    post.setViewCount(post.getViewCount() + 1);
    postRepository.save(post);

    // 태그 함께 조회
    List<Tag> tags = postTagRepository.findTagsByPostId(postId);

    PostResponse postResponse = PostResponse.of(post, tags);

    // CacheService에서 Double 타입의 평균 평점 가져오기
    Double averageRating = cacheService.getAverageRating(post.getDrink().getId());

    if (averageRating != null) {
      // 소수점 둘째 자리까지 평균 평점 조회
      String formattedRating = String.format("%.2f", averageRating);
      Double formattedAverageRating = Double.parseDouble(formattedRating);

      DrinkResponse drinkResponse = DrinkResponse.from(post.getDrink());
      drinkResponse.setAverageRating(formattedAverageRating);
      postResponse.setDrink(drinkResponse);
    }

    return postResponse;
  }

  // 게시글 삭제
  public void deletePostById(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 게시글 ID입니다."));

    Member member = memberService.getMember();

    //작성자 본인 또는 관리자만 글 삭제 가능
    if (!post.getMember().equals(member) && !member.getRole().equals(Role.MANAGER)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    postRepository.deleteById(postId);
    cacheManager.getCache("avg-rating").evict(post.getDrink().getId());
  }

  // 게시글 수정
  @Transactional
  @CacheEvict(key = "#postRequest.drinkId", value = "avg-rating")
  public PostResponse updatePost(Long postId, PostRequest postRequest) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 게시글 ID입니다."));

    post.setContent(postRequest.getContent());
    post.setRating(postRequest.getRating());

    // 음료 정보 수정할 시
    if (postRequest.getDrinkId() != null) {
      Drink drink = drinkRepository.findById(postRequest.getDrinkId())
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 특산주입니다."));
      post.setDrink(drink);
    }

    if (postRequest.getMemberId() != null) {
      Member member = memberRepository.findById(postRequest.getMemberId())
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
      post.setMember(member);
    }

    postRepository.save(post);

    // 기존 태그 삭제
    postTagRepository.deleteByPostId(postId);
    List<Tag> updateTag = saveTags(postRequest.getTag(), post);

    postRepository.save(post);

    return PostResponse.of(post, updateTag);
  }
}
