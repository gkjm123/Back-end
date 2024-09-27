package com.onedrinktoday.backend.domain.comment.controller;

import com.onedrinktoday.backend.domain.comment.dto.CommentRequest;
import com.onedrinktoday.backend.domain.comment.dto.CommentResponse;
import com.onedrinktoday.backend.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/comments")
  public ResponseEntity<CommentResponse> createComment(
      @Valid @RequestBody CommentRequest commentRequest) {

    CommentResponse commentResponse = commentService.createComment(commentRequest);
    return ResponseEntity.ok(commentResponse);
  }

  @GetMapping("/{postId}/comments")
  public ResponseEntity<Page<CommentResponse>> getAllComments(
      @PathVariable Long postId,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<CommentResponse> comments = commentService.getAllComments(postId, pageable);
    return ResponseEntity.ok(comments);
  }

  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<String> deleteCommentById(@PathVariable Long commentId) {
    commentService.deleteCommentById(commentId);
    return ResponseEntity.ok("댓글이 삭제되었습니다.");
  }

  @PutMapping("/comments/{commentId}")
  public ResponseEntity<CommentResponse> updateCommentById(
      @PathVariable Long commentId,
      @RequestBody CommentRequest commentRequest) {

    CommentResponse updatedComment = commentService.updateComment(commentId, commentRequest);
    return ResponseEntity.ok(updatedComment);
  }
}