package com.onedrinktoday.backend.domain.post.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.domain.notification.service.NotificationService;
import com.onedrinktoday.backend.domain.post.dto.PostRequest;
import com.onedrinktoday.backend.domain.post.dto.PostResponse;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.domain.postTag.entity.PostTag;
import com.onedrinktoday.backend.domain.postTag.repository.PostTagRepository;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import com.onedrinktoday.backend.domain.tag.repository.TagRepository;
import com.onedrinktoday.backend.global.cache.CacheService;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.type.Role;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

  @InjectMocks
  private PostService postService;

  @Mock
  private PostRepository postRepository;

  @Mock
  private MemberService memberService;

  @Mock
  private DrinkRepository drinkRepository;

  @Mock
  private TagRepository tagRepository;

  @Mock
  private PostTagRepository postTagRepository;

  @Mock
  private NotificationService notificationService;

  @Mock
  private CacheService cacheService;

  @Mock
  private CacheManager cacheManager;

  private PostRequest postRequest;
  private Post post;
  private Member member;
  private Region region;
  private Drink drink;
  private List<Tag> tags;

  @BeforeEach
  void setUp() {
    member = Member.builder().id(1L).name("John").role(Role.USER).build();
    region = Region.builder().id(1L).placeName("서울특별시").build();
    drink = Drink.builder().id(1L).name("막걸리").region(region).build();
    post = Post.builder().id(1L).member(member).drink(drink).content("맛있는 막걸리입니다!").viewCount(0).build();
    tags = Arrays.asList(new Tag(1L, "달콤"), new Tag(2L, "시원"));

    postRequest = new PostRequest();
    postRequest.setDrinkId(1L);
    postRequest.setContent("맛있는 막걸리입니다!");
    postRequest.setRating(4.5F);
    postRequest.setTag(Arrays.asList("달콤", "시원"));
  }

  @Test
  @DisplayName("게시글 생성 성공 테스트")
  void createPostSuccess() {
    // Given
    given(memberService.getMember()).willReturn(member);
    given(drinkRepository.findById(1L)).willReturn(Optional.of(drink));

    Tag newTag1 = Tag.builder().tagId(1L).tagName("달콤").build();
    Tag newTag2 = Tag.builder().tagId(2L).tagName("시원").build();

    given(tagRepository.findByTagName("달콤")).willReturn(Optional.of(newTag1));
    given(tagRepository.findByTagName("시원")).willReturn(Optional.of(newTag2));

    given(postRepository.save(any(Post.class))).willReturn(post);

    PostTag postTag1 = new PostTag(post, newTag1);
    PostTag postTag2 = new PostTag(post, newTag2);
    given(postTagRepository.save(any(PostTag.class))).willReturn(postTag1).willReturn(postTag2);

    ArgumentCaptor<List<Tag>> tagListCaptor = ArgumentCaptor.forClass(List.class);

    // When
    PostResponse postResponse = postService.createPost(postRequest);

    // Then
    assertNotNull(postResponse);
    assertEquals(postResponse.getContent(), "맛있는 막걸리입니다!");
    verify(notificationService).tagFollowPostNotification(eq(post.getId()), tagListCaptor.capture());
  }

  @Test
  @DisplayName("게시글 생성 실패 테스트 - Drink ID 없음")
  void createPostFailNoDrinkId() {
    // Given
    postRequest.setDrinkId(999L); // 존재하지 않는 Drink ID
    given(drinkRepository.findById(999L)).willReturn(Optional.empty());

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> postService.createPost(postRequest));
  }

  @Test
  @DisplayName("게시글 조회 성공 테스트")
  void getPostByIdSuccess() {
    // Given
    given(postRepository.findById(1L)).willReturn(Optional.of(post));
    given(postTagRepository.findTagsByPostId(1L)).willReturn(tags);
    given(cacheService.getAverageRating(1L)).willReturn(4.0);

    // When
    PostResponse postResponse = postService.getPostById(1L);

    // Then
    assertNotNull(postResponse);
    assertEquals(postResponse.getContent(), "맛있는 막걸리입니다!");
  }

  @Test
  @DisplayName("게시글 조회 실패 테스트 - 잘못된 게시글 ID")
  void getPostByIdFail() {
    // Given
    given(postRepository.findById(999L)).willReturn(Optional.empty());

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> postService.getPostById(999L));
  }

  @Test
  @DisplayName("게시글 리스트 조회 성공 테스트")
  void getPostsSuccess() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Post> postsPage = new PageImpl<>(List.of(post), pageable, 1);

    given(postRepository.findAllByOrderByViewCountDesc(pageable)).willReturn(postsPage);

    Page<PostResponse> postResponses = postService.getAllPosts(pageable, "viewCount");

    assertNotNull(postResponses);
    assertEquals(1, postResponses.getTotalElements());
    assertEquals("맛있는 막걸리입니다!", postResponses.getContent().get(0).getContent());
  }

  @Test
  @DisplayName("게시글 리스트 조회 실패 테스트 - 게시글 없음")
  void getPostsFail() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Post> emptyPage = new PageImpl<>(List.of(), pageable, 0);

    given(postRepository.findAllByOrderByViewCountDesc(pageable)).willReturn(emptyPage);

    Page<PostResponse> postResponses = postService.getAllPosts(pageable, "viewCount");

    assertNotNull(postResponses);
    assertEquals(0, postResponses.getTotalElements());
  }

  @Test
  @DisplayName("게시글 수정 성공 테스트")
  void updatePostSuccess() {
    // Given
    given(postRepository.findById(1L)).willReturn(Optional.of(post));
    given(drinkRepository.findById(1L)).willReturn(Optional.of(drink));

    Tag newTag1 = Tag.builder().tagId(1L).tagName("씁쓸").build();
    Tag newTag2 = Tag.builder().tagId(2L).tagName("새콤").build();

    // 태그 저장 로직 수정
    given(tagRepository.findByTagName("씁쓸")).willReturn(Optional.of(newTag1));
    given(tagRepository.findByTagName("새콤")).willReturn(Optional.of(newTag2));

    PostRequest updatedRequest = new PostRequest();
    updatedRequest.setDrinkId(1L);
    updatedRequest.setContent("업데이트된 내용입니다!");
    updatedRequest.setRating(5.0F);
    updatedRequest.setTag(Arrays.asList("씁쓸", "새콤"));

    // When
    PostResponse postResponse = postService.updatePost(1L, updatedRequest);

    // Then
    assertNotNull(postResponse);
    assertEquals("업데이트된 내용입니다!", postResponse.getContent());
    assertEquals(5.0F, postResponse.getRating());
    assertEquals(2, postResponse.getTags().size());
    assertEquals("씁쓸", postResponse.getTags().get(0).getTagName());
    assertEquals("새콤", postResponse.getTags().get(1).getTagName());
  }

  @Test
  @DisplayName("게시글 수정 실패 테스트 - 게시글 ID 없음")
  void updatePostFailNoPostId() {
    // Given
    PostRequest updatedRequest = new PostRequest();
    updatedRequest.setDrinkId(1L);
    updatedRequest.setContent("업데이트된 내용입니다!");
    updatedRequest.setRating(5.0F);
    updatedRequest.setTag(Arrays.asList("씁쓸", "새콤"));

    given(postRepository.findById(999L)).willReturn(Optional.empty());

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> postService.updatePost(999L, updatedRequest));
  }

  @Test
  @DisplayName("게시글 삭제 성공 테스트")
  void deletePostSuccess() {
    // Given
    given(postRepository.findById(1L)).willReturn(Optional.of(post));
    given(memberService.getMember()).willReturn(member);

    Cache cache = mock(Cache.class);
    given(cacheManager.getCache("avg-rating")).willReturn(cache);
    doNothing().when(cache).evict(1L);

    // When
    postService.deletePostById(1L);

    // Then
    verify(postRepository).deleteById(1L);
    verify(cacheManager.getCache("avg-rating")).evict(1L);
  }

  @Test
  @DisplayName("게시글 삭제 실패 테스트 - 권한 없음")
  void deletePostFail() {
    // Given
    Member anotherMember = Member.builder().id(2L).name("Jane").role(Role.USER).build();
    post.setMember(anotherMember);
    given(postRepository.findById(1L)).willReturn(Optional.of(post));
    given(memberService.getMember()).willReturn(member); // 현재 로그인된 사용자는 작성자가 아님

    // When & Then
    CustomException exception = assertThrows(CustomException.class, () -> postService.deletePostById(1L));
    assertEquals("접근이 거부되었습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("캐시에서 평균 평점 가져오기 테스트")
  void getAverageRatingFromCache() {
    // Given
    given(cacheService.getAverageRating(1L)).willReturn(4.5);

    // When
    Double rating = cacheService.getAverageRating(1L);

    // Then
    assertNotNull(rating);
    assertEquals(4.5, rating);
  }
}
