package com.onedrinktoday.backend.domain.post.controller;

import com.onedrinktoday.backend.domain.post.dto.PostRequest;
import com.onedrinktoday.backend.domain.post.dto.PostResponse;
import com.onedrinktoday.backend.domain.post.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
  private final PostService postService;

  @PostMapping("/posts")
  public ResponseEntity<PostResponse> post(@RequestHeader("member_id") Long memberId, @RequestBody PostRequest postRequest) {
    PostResponse postResponse = postService.createPost(memberId, postRequest);
    return ResponseEntity.ok(postResponse);
  }

  @GetMapping("/posts")
  public ResponseEntity<List<PostResponse>> getAllPosts() {
    List<PostResponse> posts = postService.getAllPosts();
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/posts/{postId}")
  public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
    PostResponse postResponse = postService.getPostById(postId);
    return ResponseEntity.ok(postResponse);
  }
}
