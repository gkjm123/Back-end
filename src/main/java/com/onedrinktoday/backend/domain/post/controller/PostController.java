package com.onedrinktoday.backend.domain.post.controller;

import com.onedrinktoday.backend.domain.post.dto.PostRequest;
import com.onedrinktoday.backend.domain.post.dto.PostResponse;
import com.onedrinktoday.backend.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    Page<PostResponse> posts = postService.getAllPosts(pageable, sortBy);
    return ResponseEntity.ok(posts);
  }

  // 특정 게시글 조회 API
  @GetMapping("/posts/{postId}")
  public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId,
                                                  @RequestParam(required = false, defaultValue = "false") boolean isLiked,
                                                  @RequestParam(required = false, defaultValue = "false") boolean isClicked) {
    PostResponse postResponse = postService.getPostById(postId, isLiked, isClicked);
    return ResponseEntity.ok(postResponse);
  }

  @DeleteMapping("/posts/{postId}")
  public ResponseEntity<Void> deletePostById(@PathVariable Long postId) {
    postService.deletePostById(postId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/posts/{postId}")
  public ResponseEntity<PostResponse> updatePostById(@PathVariable Long postId, @RequestBody PostRequest postRequest) {
    PostResponse updatedPost = postService.updatePost(postId, postRequest);
    return ResponseEntity.ok(updatedPost);
  }
}
