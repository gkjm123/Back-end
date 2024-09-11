package com.onedrinktoday.backend.domain.tagFollow.controller;

import com.onedrinktoday.backend.domain.tagFollow.dto.TagFollowRequest;
import com.onedrinktoday.backend.domain.tagFollow.dto.TagFollowResponse;
import com.onedrinktoday.backend.domain.tagFollow.service.TagFollowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TagFollowController {

  private final TagFollowService tagFollowService;

  @PostMapping("/tags/follows")
  public TagFollowResponse followTag(@RequestBody TagFollowRequest request) {

    return tagFollowService.followTag(request);
  }

  @GetMapping("/members/tags/follows")
  public List<TagFollowResponse> getTagFollows() {

    return tagFollowService.getTagFollows();
  }

  @DeleteMapping("/tags/follows/{followId}")
  public ResponseEntity<String> deleteTagFollow(@PathVariable Long followId) {

    tagFollowService.deleteTagFollow(followId);
    return ResponseEntity.ok("팔로우 태그가 삭제되었습니다.");
  }
}
