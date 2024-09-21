package com.onedrinktoday.backend.domain.notification.service;

import static com.onedrinktoday.backend.global.exception.ErrorCode.POST_NOT_FOUND;
import static com.onedrinktoday.backend.global.type.NotificationType.COMMENT;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.onedrinktoday.backend.domain.declaration.entity.Declaration;
import com.onedrinktoday.backend.domain.manager.dto.cancelDeclarationRequest;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.domain.notification.entity.Notification;
import com.onedrinktoday.backend.domain.notification.repository.NotificationRepository;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import com.onedrinktoday.backend.domain.tagFollow.entity.TagFollow;
import com.onedrinktoday.backend.domain.tagFollow.repository.TagFollowRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.type.NotificationType;
import com.onedrinktoday.backend.global.type.cancelDeclarationType;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private TagFollowRepository tagFollowRepository;

  @Mock
  private MemberService memberService;

  @InjectMocks
  private NotificationService notificationService;

  private Member member;
  private Post post;
  private Tag tag;
  private Declaration declaration;
  private Registration registration;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .build();
    post = Post.builder()
        .id(1L)
        .member(member)
        .build();
    tag = Tag.builder()
        .tagId(1L)
        .tagName("태그")
        .build();
    declaration = Declaration.builder()
        .member(Member.builder()
            .id(2L)
            .build())
        .build();
    registration = Registration.builder()
        .id(2L)
        .drinkName("막걸리")
        .build();
  }

  @Test
  @DisplayName("알림 생성 성공")
  void successCreateNotification() {
    //when
    notificationService.createNotification(member, 1L, COMMENT, "알림이 생성되었습니다~");

    //then
    verify(notificationRepository, times(1)).save(argThat(notification ->
        notification.getMember().equals(member) &&
            notification.getPostId().equals(1L) &&
            notification.getType().equals(COMMENT) &&
            notification.getContent().equals("알림이 생성되었습니다~")
    ));
  }

  @Test
  @DisplayName("최근 알림 조회 성공 테스트")
  void successGetRecentNotifications() {
    //given
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Order.desc("createdAt")));
    List<Notification> notifications = Arrays.asList(
        Notification.builder()
            .id(1L)
            .member(member)
            .postId(1L)
            .type(NotificationType.COMMENT)
            .content("댓글이 달렸습니다.")
            .build(),
        Notification.builder()
            .id(2L)
            .member(member)
            .postId(2L)
            .type(NotificationType.FOLLOW)
            .content("새로운 게시글이 태그와 작성되었습니다.")
            .build()
    );
    Page<Notification> notificationPage = new PageImpl<>(notifications, pageable,
        notifications.size());

    //Stubbing 설정
    given(memberService.getMember()).willReturn(member);
    given(notificationRepository.findByMemberId(1L, pageable)).willReturn(notificationPage);

    //when
    Page<Notification> result = notificationService.getRecentNotifications(pageable);

    //then
    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
    verify(notificationRepository).findByMemberId(1L, pageable);
  }

  @Test
  @DisplayName("댓글 알림 생성 성공")
  void successPostCommentNotification() {
    //given
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    //when
    notificationService.postCommentNotification(1L, "John", false);

    //then
    verify(notificationRepository, times(1)).save(argThat(notification ->
        notification.getPostId().equals(1L) &&
            notification.getMember().equals(post.getMember()) &&
            notification.getType().equals(COMMENT) &&
            notification.getContent().equals("John님이 댓글을 달았습니다.")
    ));
  }

  @Test
  @DisplayName("댓글 알림 생성 실패 - 게시글 찾을 수 없음")
  void failPostCommentNotification() {
    //given
    when(postRepository.findById(1L)).thenReturn(Optional.empty());

    //then
    assertThrows(CustomException.class,
        () -> notificationService.postCommentNotification(1L, "John", false));
  }

  @Test
  @DisplayName("태그 팔로우 게시글 알림 생성 성공")
  void successTagFollowPostNotification() {
    //given
    Member follower = Member.builder().id(1L).build();
    TagFollow tagFollow = TagFollow.builder()
        .member(follower)
        .tag(tag)
        .build();
    List<TagFollow> tagFollows = List.of(tagFollow);

    //Stubbing
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(tagFollowRepository.findByTag(tag)).thenReturn(tagFollows);

    //when
    notificationService.tagFollowPostNotification(1L, List.of(tag));

    //then
    verify(notificationRepository, times(1)).save(argThat(notification ->
        notification.getMember().equals(follower) &&
            notification.getPostId().equals(1L) &&
            notification.getType().equals(NotificationType.FOLLOW) &&
            notification.getContent().equals("새로운 게시글이 " + tag.getTagName() + " 태그와 작성되었습니다.")
    ));
  }

  @Test
  @DisplayName("태그 팔로우 게시글 알림 생성 실패 - 비동기 예외 발생")
  void failTagFollowPostNotification() {
    //given
    when(postRepository.findById(1L)).thenThrow(new CustomException(POST_NOT_FOUND));

    //when
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
      try {
        notificationService.tagFollowPostNotification(1L, List.of(tag));
        Assertions.fail("예외 발생");
      } catch (Exception e) {
        assertInstanceOf(CustomException.class, e, "예외 발생");
      }
    });

    //then
    //비동기 작업이 완료될 때까지 기다립니다.
    try {
      future.join();
    } catch (CompletionException e) {
      assertInstanceOf(CustomException.class, e.getCause(),
          "예외 발생");
    }

    verify(notificationRepository, never()).save(argThat(notification -> true));
  }

  @Test
  @DisplayName("게시글 신고 및 신고자에게 알림 생성 성공")
  void successPostDeclarationNotification() {
    //given
    //when
    notificationService.approveDeclarationNotification(post, declaration);

    //then
    verify(notificationRepository, times(1)).save(argThat(notification ->
        notification.getMember().equals(post.getMember()) &&
            notification.getPostId().equals(1L) &&
            notification.getType().equals(NotificationType.REMOVED) &&
            notification.getContent().equals("게시글이 신고되어 삭제되었습니다.")
    ));

    verify(notificationRepository, times(1)).save(argThat(notification ->
        notification.getMember().equals(declaration.getMember()) &&
            notification.getPostId() == null &&
            notification.getType().equals(NotificationType.DECLARATION) &&
            notification.getContent().equals("신고된 게시글이 승인되었습니다.")
    ));
  }

  @Test
  @DisplayName("신고 반려 알림 생성 성공")
  void successCancelDeclarationNotification() {
    //given
    String message = "신고 처리 결과를 확인하세요: " + cancelDeclarationType.POST_DELETED_BY_USER.getMessage();
    cancelDeclarationRequest request = cancelDeclarationRequest.builder()
        .type(cancelDeclarationType.POST_DELETED_BY_USER)
        .build();

    declaration = Declaration.builder()
        .member(member)
        .id(1L)
        .build();

    //when
    notificationService.cancelDeclarationNotification(declaration, request);

    //then
    verify(notificationRepository, times(1)).save(argThat(notification ->
        notification.getMember().equals(declaration.getMember()) &&
            notification.getPostId().equals(declaration.getId()) &&
            notification.getType().equals(NotificationType.REJECTION) &&
            notification.getContent().equals(message)
    ));
  }

  @Test
  @DisplayName("특산주 등록 승인 시 알림 생성 성공")
  void successApproveRegistrationNotification() {
    //given
    Member member = new Member();
    member.setId(1L);

    //when
    notificationService.approveRegistrationNotification(member, registration);

    //then
    verify(notificationRepository, times(1)).save(argThat(notification ->
        notification.getMember().equals(member) &&
            notification.getPostId().equals(2L) &&
            notification.getType().equals(NotificationType.REGISTRATION) &&
            notification.getContent().equals("신청된 막걸리 특산주가 승인되었습니다.")
    ));
  }
}