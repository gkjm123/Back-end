package com.onedrinktoday.backend.domain.comment.controller;

import static com.onedrinktoday.backend.global.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedrinktoday.backend.domain.comment.dto.CommentRequest;
import com.onedrinktoday.backend.domain.comment.dto.CommentResponse;
import com.onedrinktoday.backend.domain.comment.service.CommentService;
import com.onedrinktoday.backend.global.exception.CustomException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(CommentController.class)
public class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CommentService commentService;

  @Autowired
  private ObjectMapper objectMapper;

  private CommentRequest commentRequest;
  private CommentResponse commentResponse;
  private Page<CommentResponse> commentsPage;

  @BeforeEach
  void setUp() {

    commentRequest = CommentRequest.builder()
        .postId(1L)
        .content("맛있어 보이네요. 저도 먹어보고 싶어요.")
        .anonymous(true)
        .build();

    commentResponse = CommentResponse.builder()
        .id(1L)
        .postId(commentRequest.getPostId())
        .content(commentRequest.getContent())
        .anonymous(commentRequest.isAnonymous())
        .build();

    commentsPage = new PageImpl<>(Collections.singletonList(commentResponse));
  }

  @Test
  @DisplayName("댓글 생성 성공")
  void successCreateComment() throws Exception {
    //given
    when(commentService.createComment(any(CommentRequest.class))).thenReturn(commentResponse);

    //then
    //when
    mockMvc.perform(post("/api/comments")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(commentResponse.getId()))
        .andExpect(jsonPath("$.content").value(commentResponse.getContent()))
        .andExpect(jsonPath("$.anonymous").value(commentResponse.isAnonymous()))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 생성 실패 - 게시글 없음")
  void failCreateComment() throws Exception {
    //given
    when(commentService.createComment(any(CommentRequest.class)))
        .thenThrow(new CustomException(POST_NOT_FOUND));

    //when
    //then
    mockMvc.perform(post("/api/comments")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("댓글 목록 조회 성공")
  void successGetAllComments() throws Exception {
    //given
    when(commentService.getAllComments(anyLong(), any(Pageable.class))).thenReturn(commentsPage);

    //when
    //then
    mockMvc.perform(get("/api/1/comments")
            .with(csrf())
            .param("page", "0")
            .param("size", "10")
            .param("sort", "createdAt,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(commentResponse.getId()))
        .andExpect(jsonPath("$.content[0].content").value(commentResponse.getContent()))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 존재하지 않는 게시글")
  void failGetAllComments() throws Exception {
    //given
    when(commentService.getAllComments(anyLong(), any(Pageable.class)))
        .thenThrow(new CustomException(POST_NOT_FOUND));

    //when
    //then
    mockMvc.perform(get("/api/1212/comments")
            .with(csrf())
            .param("page", "0")
            .param("size", "10")
            .param("sort", "createdAt,desc"))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("댓글 삭제 성공")
  void successDeleteComment() throws Exception {
    //given
    doNothing().when(commentService).deleteCommentById(anyLong());

    //when
    //then
    mockMvc.perform(delete("/api/comments/1")
            .with(csrf()))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 삭제 실패 - 댓글 없음")
  void failDeleteComment() throws Exception {
    //given
    doThrow(new CustomException(COMMENT_NOT_FOUND)).when(commentService)
        .deleteCommentById(anyLong());

    //when
    //then
    mockMvc.perform(delete("/api/comments/1")
            .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("댓글 수정 성공")
  void successUpdateComment() throws Exception {
    //given
    CommentRequest updatedCommentRequest = CommentRequest.builder()
        .content("제 취향은 아니네요")
        .anonymous(false)
        .build();

    CommentResponse updatedCommentResponse = CommentResponse.builder()
        .id(1L)
        .postId(1L)
        .content(updatedCommentRequest.getContent())
        .anonymous(updatedCommentRequest.isAnonymous())
        .build();

    when(commentService.updateComment(anyLong(), any(CommentRequest.class))).thenReturn(
        updatedCommentResponse);

    //when
    //then
    mockMvc.perform(put("/api/comments/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedCommentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedCommentResponse.getId()))
        .andExpect(jsonPath("$.content").value(updatedCommentResponse.getContent()))
        .andExpect(jsonPath("$.anonymous").value(updatedCommentResponse.isAnonymous()))
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 수정 실패 - 댓글 없음")
  void failUpdateComment() throws Exception {
    //given
    CommentRequest commentRequest = CommentRequest.builder()
        .content("제 취향은 아니네요")
        .anonymous(false)
        .build();

    when(commentService.updateComment(anyLong(), any(CommentRequest.class)))
        .thenThrow(new CustomException(COMMENT_NOT_FOUND));

    //when
    //then
    mockMvc.perform(put("/api/comments/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentRequest)))
        .andExpect(status().isNotFound());
  }
}