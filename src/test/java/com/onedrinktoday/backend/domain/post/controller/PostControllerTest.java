package com.onedrinktoday.backend.domain.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedrinktoday.backend.domain.post.dto.PostRequest;
import com.onedrinktoday.backend.domain.post.dto.PostResponse;
import com.onedrinktoday.backend.domain.post.service.PostService;
import com.onedrinktoday.backend.domain.tag.dto.TagDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

@WebMvcTest(controllers = PostController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class PostControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PostService postService;

  @Autowired
  private ObjectMapper objectMapper;

  private PostRequest postRequest;
  private PostResponse postResponse;

  @BeforeEach
  void setUp() {
    postRequest = new PostRequest();
    postRequest.setDrinkId(1L);
    postRequest.setContent("맛있는 막걸리입니다!");
    postRequest.setRating(4.5F);
    postRequest.setTag(Arrays.asList("달콤", "시원"));

    TagDTO tag1 = new TagDTO();
    tag1.setTagName("달콤");

    TagDTO tag2 = new TagDTO();
    tag2.setTagName("시원");

    List<TagDTO> tagDTOList = Arrays.asList(tag1, tag2);
    postResponse = new PostResponse();
    postResponse.setContent("맛있는 막걸리입니다!");
    postResponse.setRating(4.5F);
    postResponse.setTags(tagDTOList);
  }

  @Test
  @DisplayName("게시글 생성 성공 테스트")
  void successCreatePost() throws Exception {
    given(postService.createPost(any(PostRequest.class))).willReturn(postResponse);

    mockMvc.perform(post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value(postRequest.getContent()))
        .andExpect(jsonPath("$.rating").value(postRequest.getRating()))
        .andExpect(jsonPath("$.tags[0].tagName").value("달콤"))
        .andExpect(jsonPath("$.tags[1].tagName").value("시원"));
  }

  // 성공 테스트
  @Test
  @DisplayName("게시글 조회 성공 테스트")
  void successGetPostById() throws Exception {
    given(postService.getPostById(1L)).willReturn(postResponse);

    mockMvc.perform(get("/api/post/{postId}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value(postResponse.getContent()))
        .andExpect(jsonPath("$.rating").value(postResponse.getRating()))
        .andExpect(jsonPath("$.tags[0].tagName").value("달콤"))
        .andExpect(jsonPath("$.tags[1].tagName").value("시원"));
  }

  @Test
  @DisplayName("게시글 조회 실패 테스트 - 존재하지 않는 게시글")
  void failNotFoundGetPostById() throws Exception {
    given(postService.getPostById(999L))
        .willThrow(new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

    mockMvc.perform(get("/api/post/{postId}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("해당 게시글을 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("게시글 리스트 조회 성공 테스트")
  void successGetAllPosts() throws Exception {
    Page<PostResponse> postsPage = new PageImpl<>(Arrays.asList(postResponse), PageRequest.of(0, 10), 1);
    given(postService.getAllPosts(any(PageRequest.class), eq("createdAt"))).willReturn(postsPage);

    mockMvc.perform(get("/api/posts?page=0&size=10&sortBy=createdAt"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value(postResponse.getContent()))
        .andExpect(jsonPath("$.content[0].tags[0].tagName").value("달콤"))
        .andExpect(jsonPath("$.content[0].tags[1].tagName").value("시원"));
  }

  @Test
  @DisplayName("게시글 수정 성공 테스트")
  void successUpdatePostById() throws Exception {
    PostRequest updateRequest = new PostRequest();
    updateRequest.setDrinkId(1L);
    updateRequest.setContent("맛없는 막걸리입니다!");
    updateRequest.setRating(2.0F);
    updateRequest.setTag(Arrays.asList("씁쓸", "새콤"));

    TagDTO tag3 = new TagDTO();
    tag3.setTagName("씁쓸");

    TagDTO tag4 = new TagDTO();
    tag4.setTagName("새콤");

    List<TagDTO> updatedTags = Arrays.asList(tag3, tag4);
    PostResponse updatedResponse = new PostResponse();
    updatedResponse.setContent("맛없는 막걸리입니다!");
    updatedResponse.setRating(2.0F);
    updatedResponse.setTags(updatedTags);

    given(postService.updatePost(eq(1L), any(PostRequest.class))).willReturn(updatedResponse);

    mockMvc.perform(put("/api/posts/{postId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("맛없는 막걸리입니다!"))
        .andExpect(jsonPath("$.tags[0].tagName").value("씁쓸"))
        .andExpect(jsonPath("$.tags[1].tagName").value("새콤"));
  }

  @Test
  @DisplayName("게시글 수정 실패 테스트 - 존재하지 않는 게시글")
  void failNotFoundUpdatePostById() throws Exception {
    PostRequest updateRequest = new PostRequest();
    updateRequest.setDrinkId(1L);
    updateRequest.setContent("맛없는 막걸리입니다!");
    updateRequest.setRating(2.0F);
    updateRequest.setTag(Arrays.asList("씁쓸", "새콤"));

    given(postService.updatePost(eq(999L), any(PostRequest.class)))
        .willThrow(new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

    mockMvc.perform(put("/api/posts/{postId}", 999L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("해당 게시글을 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("게시글 삭제 성공 테스트")
  void successDeletePostById() throws Exception {
    doNothing().when(postService).deletePostById(1L);

    mockMvc.perform(delete("/api/posts/{postId}", 1L))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("게시글 삭제 실패 테스트 - 존재하지 않는 게시글")
  void failNotFoundDeletePostById() throws Exception {
    doThrow(new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."))
        .when(postService).deletePostById(999L);

    mockMvc.perform(delete("/api/posts/{postId}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("해당 게시글을 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("게시글 좋아요 성공 테스트")
  void successLikePost() throws Exception {
    doNothing().when(postService).likePost(1L, false);

    mockMvc.perform(put("/api/posts/{postId}/like", 1L))
        .andExpect(status().isOk());
  }
}
