package com.onedrinktoday.backend.domain.tagFollow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedrinktoday.backend.domain.tagFollow.dto.TagFollowRequest;
import com.onedrinktoday.backend.domain.tagFollow.dto.TagFollowResponse;
import com.onedrinktoday.backend.domain.tagFollow.service.TagFollowService;
import com.onedrinktoday.backend.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.onedrinktoday.backend.global.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WithMockUser
@WebMvcTest(TagFollowController.class)
public class TagFollowControllerTest {

  @MockBean
  private TagFollowService tagFollowService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private TagFollowRequest tagFollowRequest;
  private TagFollowResponse tagFollowResponse;

  @BeforeEach
  void setUp() {
    tagFollowRequest = new TagFollowRequest();
    tagFollowRequest.setTagId(1L);

    tagFollowResponse = TagFollowResponse.builder()
        .followId(1L)
        .memberId(1L)
        .memberName("John")
        .tagId(1L)
        .tagName("spicy")
        .build();
  }

  @Test
  @DisplayName("태그 팔로우 성공")
  void successFollowTag() throws Exception {
    //given
    given(tagFollowService.followTag(any(TagFollowRequest.class))).willReturn(tagFollowResponse);

    //when
    //then
    mockMvc.perform(post("/api/tags/follows")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(tagFollowRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.followId").value(tagFollowResponse.getFollowId()))
        .andExpect(jsonPath("$.memberId").value(tagFollowResponse.getMemberId()))
        .andExpect(jsonPath("$.memberName").value(tagFollowResponse.getMemberName()))
        .andExpect(jsonPath("$.tagId").value(tagFollowResponse.getTagId()))
        .andExpect(jsonPath("$.tagName").value(tagFollowResponse.getTagName()))
        .andDo(print());
  }

  @Test
  @DisplayName("태그 팔로우 실패 - 태그가 존재하지 않음")
  void failFollowTag() throws Exception {
    //given
    given(tagFollowService.followTag(any(TagFollowRequest.class)))
        .willThrow(new CustomException(TAG_NOT_FOUND));

    //when
    //then
    mockMvc.perform(post("/api/tags/follows")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(tagFollowRequest)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(TAG_NOT_FOUND.getMessage()))
        .andDo(print());
  }

  @Test
  @DisplayName("태그 팔로우 목록 조회 성공")
  void successGetTagFollows() throws Exception {
    //given
    given(tagFollowService.getTagFollows(anyLong())).willReturn(List.of(tagFollowResponse));

    //when
    //then
    mockMvc.perform(get("/api/members/1/tags/follows")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].followId").value(tagFollowResponse.getFollowId()))
        .andExpect(jsonPath("$[0].memberId").value(tagFollowResponse.getMemberId()))
        .andExpect(jsonPath("$[0].memberName").value(tagFollowResponse.getMemberName()))
        .andExpect(jsonPath("$[0].tagId").value(tagFollowResponse.getTagId()))
        .andExpect(jsonPath("$[0].tagName").value(tagFollowResponse.getTagName()))
        .andDo(print());
  }

  @Test
  @DisplayName("태그 팔로우 목록 조회 실패 - 회원을 찾을 수 없음")
  void failGetTagFollows() throws Exception {
    //given
    given(tagFollowService.getTagFollows(anyLong())).willThrow(
        new CustomException(MEMBER_NOT_FOUND));

    //when
    //then
    mockMvc.perform(get("/api/members/1/tags/follows")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(MEMBER_NOT_FOUND.getMessage()))
        .andDo(print());
  }

  @Test
  @DisplayName("태그 팔로우 삭제 성공")
  void successDeleteTagFollow() throws Exception {
    //when
    //then
    mockMvc.perform(delete("/api/tags/follows/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("팔로우 태그가 삭제되었습니다."))
        .andDo(print());
  }

  @Test
  @DisplayName("태그 팔로우 삭제 실패 - 접근이 거부됨")
  void failDeleteTagFollow() throws Exception {
    //given
    doThrow(new CustomException(ACCESS_DENIED)).when(tagFollowService).deleteTagFollow(anyLong());

    //when
    //then
    mockMvc.perform(delete("/api/tags/follows/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(content().string(ACCESS_DENIED.getMessage()))
        .andDo(print());
  }
}