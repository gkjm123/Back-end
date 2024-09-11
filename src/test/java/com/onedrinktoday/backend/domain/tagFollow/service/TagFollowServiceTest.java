package com.onedrinktoday.backend.domain.tagFollow.service;

import static com.onedrinktoday.backend.global.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TagFollowServiceTest {

  @InjectMocks
  private TagFollowService tagFollowService;

  @Mock
  private MemberService memberService;

  @Mock
  private TagRepository tagRepository;

  @Mock
  private TagFollowRepository tagFollowRepository;

  private TagFollow tagFollow;
  private Member member;
  private Tag tag;
  private TagFollowRequest tagFollowRequest;

  @BeforeEach
  void setUp() {
    member = new Member();
    member.setId(1L);
    member.setName("John");

    tag = new Tag();
    tag.setTagId(1L);
    tag.setTagName("spicy");

    tagFollow = TagFollow.builder()
        .id(1L)
        .member(member)
        .tag(tag)
        .build();

    tagFollowRequest = new TagFollowRequest();
    tagFollowRequest.setTagId(1L);
  }

  @Test
  @DisplayName("태그 팔로우 성공")
  void successFollowTag() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
    when(tagFollowRepository.save(any(TagFollow.class))).thenReturn(tagFollow);

    //when
    TagFollowResponse response = tagFollowService.followTag(tagFollowRequest);

    //then
    assertNotNull(response);
    assertEquals(tagFollow.getMember().getId(), response.getMemberId());
    assertEquals(tagFollow.getMember().getName(), response.getMemberName());
    assertEquals(tagFollow.getTag().getTagId(), response.getTagId());
    assertEquals(tagFollow.getTag().getTagName(), response.getTagName());
  }

  @Test
  @DisplayName("태그 팔로우 실패 - 태그를 찾을 수 없음")
  void failFollowTag() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

    //when
    CustomException thrown = assertThrows(CustomException.class,
        () -> tagFollowService.followTag(tagFollowRequest));

    //then
    assertEquals(TAG_NOT_FOUND, thrown.getErrorCode());
  }

  @Test
  @DisplayName("팔로우한 태그 조회 성공")
  void successGetTagFollows() {
    //given
    given(memberService.getMember()).willReturn(member);
    when(tagFollowRepository.findByMember(member)).thenReturn(List.of(tagFollow));

    //when
    List<TagFollowResponse> responses = tagFollowService.getTagFollows();

    //then
    TagFollowResponse response = responses.get(0);
    assertEquals(tagFollow.getId(), response.getId());
    assertEquals(member.getId(), response.getMemberId());
    assertEquals(member.getName(), response.getMemberName());
    assertEquals(tag.getTagId(), response.getTagId());
    assertEquals(tag.getTagName(), response.getTagName());
  }

  @Test
  @DisplayName("팔로우한 태그 조회 실패 - 회원을 찾을 수 없음")
  void failGetTagFollows() {
    //given
    given(memberService.getMember()).willThrow(new CustomException(MEMBER_NOT_FOUND));

    //when
    CustomException thrown = assertThrows(CustomException.class,
        () -> tagFollowService.getTagFollows());

    //then
    assertEquals(MEMBER_NOT_FOUND, thrown.getErrorCode());
  }

  @Test
  @DisplayName("팔로우한 태그 삭제 성공")
  void successDeleteTagFollow() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(tagFollowRepository.findById(anyLong())).thenReturn(Optional.of(tagFollow));

    //when
    tagFollowService.deleteTagFollow(1L);
  }

  @Test
  @DisplayName("팔로우한 태그 삭제 실패 - 다른 사용자")
  void failDeleteTagFollow() {
    //given
    Member otherMember = new Member();
    otherMember.setId(2L);
    otherMember.setName("Other Member");

    when(memberService.getMember()).thenReturn(otherMember);
    when(tagFollowRepository.findById(anyLong())).thenReturn(Optional.of(tagFollow));

    //when
    CustomException thrown = assertThrows(CustomException.class,
        () -> tagFollowService.deleteTagFollow(1L));

    //then
    assertEquals(ACCESS_DENIED, thrown.getErrorCode());
  }
}