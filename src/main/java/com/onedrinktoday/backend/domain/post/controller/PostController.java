package com.onedrinktoday.backend.domain.post.controller;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.post.dto.PostRequest;
import com.onedrinktoday.backend.domain.post.dto.PostResponse;
import com.onedrinktoday.backend.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
  private final PostService postService;

  @PostMapping("/posts")
  public ResponseEntity<PostResponse> post(@RequestBody PostRequest postRequest) {
    PostResponse postResponse = postService.createPost(postRequest);
    return ResponseEntity.ok(postResponse);
  }

  @GetMapping("/posts")
  public ResponseEntity<Page<PostResponse>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "createdAt") String sortBy) {
    Pageable pageable = PageRequest.of(page, size);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Member member = (Member) authentication.getPrincipal(); // 사용자 정보 Member로 캐스팅

    Page<PostResponse> posts = postService.getAllPosts(pageable, sortBy, member.getId());
    return ResponseEntity.ok(posts);
  }

  // 특정 게시글 조회 API
  @GetMapping("/post/{postId}")
  public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
    PostResponse postResponse = postService.getPostById(postId);
    return ResponseEntity.ok(postResponse);
  }

  @DeleteMapping("/posts/{postId}")
  public ResponseEntity<String> deletePostById(@PathVariable Long postId) {
    postService.deletePostById(postId);
    return ResponseEntity.ok("게시글이 삭제되었습니다.");
  }

  @PutMapping("/posts/{postId}")
  public ResponseEntity<PostResponse> updatePostById(@PathVariable Long postId, @RequestBody PostRequest postRequest) {
    PostResponse updatedPost = postService.updatePost(postId, postRequest);
    return ResponseEntity.ok(updatedPost);
  }

  // 좋아요 토글 API
  @PutMapping("posts/{postId}/like")
  public ResponseEntity<Void> likePost(@PathVariable Long postId) {
    postService.toggleLike(postId);
    return ResponseEntity.ok().build();
  }
}
