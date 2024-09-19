package com.onedrinktoday.backend.domain.announcement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedrinktoday.backend.domain.announcement.dto.AnnouncementRequest;
import com.onedrinktoday.backend.domain.announcement.dto.AnnouncementResponse;
import com.onedrinktoday.backend.domain.announcement.service.AnnouncementService;
import java.time.LocalDateTime;
import java.util.Collections;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AnnouncementController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
public class AnnouncementControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AnnouncementService announcementService;

  @Autowired
  private ObjectMapper objectMapper;

  private AnnouncementRequest announcementRequest;
  private AnnouncementResponse announcementResponse;

  @BeforeEach
  void setUp() {
    announcementRequest = new AnnouncementRequest();
    announcementRequest.setTitle("새로운 공지");
    announcementRequest.setContent("공지 내용입니다.");

    announcementResponse = AnnouncementResponse.builder()
        .id(1L)
        .memberId(1L)
        .title("새로운 공지")
        .content("공지 내용입니다.")
        .imageUrl("http://image")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("공지사항 생성 성공")
  void successCreateAnnouncement() throws Exception {
    //given
    given(announcementService.createAnnouncement(any(AnnouncementRequest.class)))
        .willReturn(announcementResponse);

    //when, then
    mockMvc.perform(post("/api/announcements")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(announcementRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(announcementResponse.getId()))
        .andExpect(jsonPath("$.title").value(announcementResponse.getTitle()))
        .andExpect(jsonPath("$.content").value(announcementResponse.getContent()));
  }

  @Test
  @DisplayName("공지사항 조회 성공")
  void successGetAnnouncement() throws Exception {
    //given
    given(announcementService.getAnnouncement(eq(1L))).willReturn(announcementResponse);

    //when, then
    mockMvc.perform(get("/api/announcements/{announcementId}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(announcementResponse.getId()))
        .andExpect(jsonPath("$.title").value(announcementResponse.getTitle()))
        .andExpect(jsonPath("$.content").value(announcementResponse.getContent()));
  }

  @Test
  @DisplayName("공지사항 조회 실패 - 공지사항 존재하지 않음")
  void failGetAnnouncement() throws Exception {
    //given
    given(announcementService.getAnnouncement(eq(1010L)))
        .willThrow(new IllegalArgumentException("공지사항을 찾을수 없습니다."));

    //when, then
    mockMvc.perform(get("/api/announcements/{announcementId}", 1010L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("공지사항을 찾을수 없습니다."));
  }

  @Test
  @DisplayName("공지사항 전체 조회 성공")
  void successGetAllAnnouncements() throws Exception {
    //given
    Page<AnnouncementResponse> announcementsPage = new PageImpl<>(
        Collections.singletonList(announcementResponse),
        PageRequest.of(0, 10, Sort.by("createdAt").descending()), 1);
    given(announcementService.getAllAnnouncements(
        PageRequest.of(0, 10, Sort.by("createdAt").descending())))
        .willReturn(announcementsPage);

    //when, then
    mockMvc.perform(get("/api/announcements"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(announcementResponse.getId()))
        .andExpect(jsonPath("$.content[0].title").value(announcementResponse.getTitle()))
        .andExpect(jsonPath("$.content[0].content").value(announcementResponse.getContent()));
  }

  @Test
  @DisplayName("공지사항 수정 성공")
  void successUpdateAnnouncement() throws Exception {
    //given
    AnnouncementRequest updateRequest = new AnnouncementRequest();
    updateRequest.setTitle("수정된 공지");
    updateRequest.setContent("수정된 내용입니다.");

    AnnouncementResponse updatedResponse = AnnouncementResponse.builder()
        .id(1L)
        .memberId(1L)
        .title("수정된 공지")
        .content("수정된 내용입니다.")
        .createdAt(announcementResponse.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .build();

    given(announcementService.updateAnnouncement(eq(1L), any(AnnouncementRequest.class)))
        .willReturn(updatedResponse);

    //when, then
    mockMvc.perform(put("/api/announcements/{announcementId}", 1L)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedResponse.getId()))
        .andExpect(jsonPath("$.title").value(updatedResponse.getTitle()))
        .andExpect(jsonPath("$.content").value(updatedResponse.getContent()));
  }

  @Test
  @DisplayName("공지사항 수정 실패 - 공지사항 존재하지 않음")
  void failUpdateAnnouncement() throws Exception {
    //given
    AnnouncementRequest updateRequest = new AnnouncementRequest();
    updateRequest.setTitle("수정된 공지");
    updateRequest.setContent("수정된 내용입니다.");

    given(announcementService.updateAnnouncement(eq(1010L), any(AnnouncementRequest.class)))
        .willThrow(new IllegalArgumentException("공지사항을 찾을수 없습니다."));

    //when, then
    mockMvc.perform(put("/api/announcements/{announcementId}", 1010L)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("공지사항을 찾을수 없습니다."));
  }

  @Test
  @DisplayName("공지사항 삭제 성공")
  void successDeleteAnnouncement() throws Exception {
    //given
    doNothing().when(announcementService).deleteAnnouncement(1L);

    //when,then
    mockMvc.perform(delete("/api/announcements/{announcementId}", 1L))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("공지사항 삭제 실패 - 공지사항 존재하지 않음")
  void failDeleteAnnouncement() throws Exception {
    //given
    doThrow(new IllegalArgumentException("공지사항을 찾을수 없습니다."))
        .when(announcementService).deleteAnnouncement(1010L);

    //when, then
    mockMvc.perform(delete("/api/announcements/{announcementId}", 1010L)
            .with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("공지사항을 찾을수 없습니다."));
  }
}