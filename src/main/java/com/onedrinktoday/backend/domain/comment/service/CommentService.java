package com.onedrinktoday.backend.domain.comment.service;

import static com.onedrinktoday.backend.global.exception.ErrorCode.*;

import com.onedrinktoday.backend.domain.comment.dto.CommentRequest;
import com.onedrinktoday.backend.domain.comment.dto.CommentResponse;
import com.onedrinktoday.backend.domain.comment.entity.Comment;
import com.onedrinktoday.backend.domain.comment.repository.CommentRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final MemberService memberService;

  public CommentResponse createComment(CommentRequest commentRequest) {

    Member member = memberService.getMember();

    Post post = postRepository.findById(commentRequest.getPostId())
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

    Comment comment = Comment.builder()
        .member(member)
        .post(post)
        .content(commentRequest.getContent())
        .anonymous(commentRequest.getAnonymous())
        .build();

    commentRepository.save(comment);

    return CommentResponse.from(comment);
  }

  public Page<CommentResponse> getAllComments(Long postId, Pageable pageable) {

    return commentRepository.findByPostId(postId, pageable)
        .map(CommentResponse::from);
  }

  public void deleteCommentById(Long commentId) {

    Member member = memberService.getMember();

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    if (!comment.getMember().equals(member)) {
      throw new CustomException(ACCESS_DENIED);
    }

    commentRepository.delete(comment);
  }

  public CommentResponse updateComment(Long commentId, CommentRequest commentRequest) {

    Member member = memberService.getMember();

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    if (!comment.getMember().equals(member)) {
      throw new CustomException(ACCESS_DENIED);
    }

    comment.setContent(commentRequest.getContent());
    comment.setAnonymous(commentRequest.getAnonymous());

    return CommentResponse.from(commentRepository.save(comment));
  }
}