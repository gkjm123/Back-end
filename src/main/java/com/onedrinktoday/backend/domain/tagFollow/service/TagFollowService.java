package com.onedrinktoday.backend.domain.tagFollow.service;

import static com.onedrinktoday.backend.global.exception.ErrorCode.*;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import com.onedrinktoday.backend.domain.tag.repository.TagRepository;
import com.onedrinktoday.backend.domain.tagFollow.dto.TagFollowRequest;
import com.onedrinktoday.backend.domain.tagFollow.dto.TagFollowResponse;
import com.onedrinktoday.backend.domain.tagFollow.entity.TagFollow;
import com.onedrinktoday.backend.domain.tagFollow.repository.TagFollowRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class TagFollowService {

  private final MemberService memberService;
  private final TagFollowRepository tagFollowRepository;
  private final TagRepository tagRepository;

  public TagFollowResponse followTag(TagFollowRequest request) {

    Member member = memberService.getMember();

    Tag tag = tagRepository.findById(request.getTagId())
        .orElseThrow(() -> new CustomException(TAG_NOT_FOUND));

    if (tagFollowRepository.existsByMemberAndTag(member, tag)) {
      throw new CustomException(ALREADY_FOLLOWING);
    }

    TagFollow tagFollow = TagFollow.builder()
        .member(member)
        .tag(tag)
        .build();

    tagFollowRepository.save(tagFollow);
    return TagFollowResponse.from(tagFollow);
  }

  public List<TagFollowResponse> getTagFollows() {

    Member member = memberService.getMember();

    List<TagFollow> tagFollows = tagFollowRepository.findByMember(member);

    return tagFollows.stream()
        .map(TagFollowResponse::from)
        .toList();
  }

  public void deleteTagFollow(Long followId) {

    Member member = memberService.getMember();

    TagFollow tagFollow = tagFollowRepository.findById(followId)
        .orElseThrow(() -> new CustomException(TAG_NOT_FOUND));

    if (!tagFollow.getMember().equals(member)) {
      throw new CustomException(ACCESS_DENIED);
    }

    tagFollowRepository.delete(tagFollow);
  }
}