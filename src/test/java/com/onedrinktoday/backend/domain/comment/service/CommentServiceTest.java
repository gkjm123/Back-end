package com.onedrinktoday.backend.domain.comment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.onedrinktoday.backend.domain.comment.dto.CommentRequest;
import com.onedrinktoday.backend.domain.comment.dto.CommentResponse;
import com.onedrinktoday.backend.domain.comment.entity.Comment;
import com.onedrinktoday.backend.domain.comment.repository.CommentRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

  @InjectMocks
  private CommentService commentService;

  @Mock
  private MemberService memberService;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private PostRepository postRepository;

  private CommentRequest commentRequest;
  private Comment comment;
  private Member member;
  private Post post;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .name("John Doe")
        .build();

    post = Post.builder().
        id(1L)
        .build();

    comment = Comment.builder()
        .id(1L)
        .member(member)
        .post(post)
        .content("맛있어 보이네요")
        .anonymous(true)
        .build();

    commentRequest = CommentRequest.builder()
        .postId(post.getId())
        .content("맛있어 보이네요")
        .anonymous(true)
        .build();
  }

  @Test
  @DisplayName("댓글 생성 성공")
  void successCreateComment() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(postRepository.findById(commentRequest.getPostId())).thenReturn(Optional.of(post));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    //when
    CommentResponse response = commentService.createComment(commentRequest);

    //then
    assertEquals(comment.getPost().getId(), response.getPostId());
    assertEquals(comment.getContent(), response.getContent());
    assertEquals(comment.isAnonymous(), response.isAnonymous());
  }

  @Test
  @DisplayName("댓글 생성 실패 - 게시글 없음")
  void failCreateComment() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(postRepository.findById(commentRequest.getPostId())).thenReturn(Optional.empty());

    //when
    //then
    assertThrows(CustomException.class, () -> commentService.createComment(commentRequest));
  }

  @Test
  @DisplayName("댓글 조회 성공")
  void successGetAllComments() {
    //given
    Pageable pageable = PageRequest.of(0, 10);
    Page<Comment> comments = new PageImpl<>(List.of(comment));

    when(commentRepository.findByPostId(post.getId(), pageable)).thenReturn(comments);

    //when
    Page<CommentResponse> response = commentService.getAllComments(post.getId(), pageable);

    //then
    assertEquals(comment.getContent(), response.getContent().get(0).getContent());
  }

  @Test
  @DisplayName("댓글 조회 실패 - 게시글 없음")
  void failGetAllComments() {
    //given
    Pageable pageable = PageRequest.of(0, 10);
    Page<Comment> emptyComments = Page.empty();

    when(commentRepository.findByPostId(post.getId(), pageable)).thenReturn(emptyComments);

    //when
    Page<CommentResponse> response = commentService.getAllComments(post.getId(), pageable);

    //then
    assertTrue(response.isEmpty());
  }

  @Test
  @DisplayName("댓글 삭제 성공")
  void successDeleteComment() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

    //when
    commentService.deleteCommentById(comment.getId());

    //then
  }

  @Test
  @DisplayName("댓글 삭제 실패 - 댓글 없음")
  void failDeleteComment() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());

    //when
    //then
    assertThrows(CustomException.class, () -> commentService.deleteCommentById(comment.getId()));
  }

  @Test
  @DisplayName("댓글 수정 성공")
  void successUpdateComment() {
    //given
    String newContent = "제 취향은 아니네요";
    boolean isAnonymous = false;

    CommentRequest commentRequest = CommentRequest.builder()
        .postId(post.getId())
        .content(newContent)
        .anonymous(isAnonymous)
        .build();

    Comment updatedComment = Comment.builder()
        .id(comment.getId())
        .member(member)
        .post(post)
        .content(newContent)
        .anonymous(isAnonymous)
        .build();

    when(memberService.getMember()).thenReturn(member);
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
    when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);

    //when
    CommentResponse response = commentService.updateComment(comment.getId(), commentRequest);

    //then
    assertEquals(newContent, response.getContent());
    assertEquals(isAnonymous, response.isAnonymous());
  }

  @Test
  @DisplayName("댓글 수정 실패 - 댓글 없음")
  void failUpdateComment() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());

    //when
    //then
    assertThrows(CustomException.class,
        () -> commentService.updateComment(comment.getId(), commentRequest));
  }
}
