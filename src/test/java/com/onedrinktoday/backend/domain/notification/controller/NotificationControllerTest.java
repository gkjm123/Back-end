package com.onedrinktoday.backend.domain.notification.controller;

import com.onedrinktoday.backend.domain.notification.entity.Notification;
import com.onedrinktoday.backend.domain.notification.service.NotificationService;
import com.onedrinktoday.backend.global.type.NotificationType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private NotificationService notificationService;

  private Page<Notification> notificationPage;

  @BeforeEach
  public void setUp() {
    Notification notification1 = Notification.builder()
        .id(1L)
        .postId(10L)
        .type(NotificationType.COMMENT)
        .content("게시글에 댓글이 달렸습니다.")
        .createdAt(LocalDateTime.now())
        .build();

    Notification notification2 = Notification.builder()
        .id(2L)
        .postId(11L)
        .type(NotificationType.FOLLOW)
        .content("새로운 게시글이 달콤 태그와 작성되었습니다.")
        .createdAt(LocalDateTime.now())
        .build();

    notificationPage = new PageImpl<>(Arrays.asList(notification1, notification2));
  }

  @Test
  @DisplayName("알림 조회 성공")
  void getRecentNotifications() throws Exception {
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

    when(notificationService.getRecentNotifications(pageable)).thenReturn(notificationPage);

    mockMvc.perform(get("/api/notifications")
            .with(csrf())
            .with(user("John").roles("USER"))
            .param("page", "0")
            .param("size", "20")
            .param("sort", "createdAt,DESC"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1L))
        .andExpect(jsonPath("$.content[0].postId").value(10L))
        .andExpect(jsonPath("$.content[0].type").value("COMMENT"))
        .andExpect(jsonPath("$.content[0].content").value("게시글에 댓글이 달렸습니다."))
        .andExpect(jsonPath("$.content[1].id").value(2L))
        .andExpect(jsonPath("$.content[1].postId").value(11L))
        .andExpect(jsonPath("$.content[1].type").value("FOLLOW"))
        .andExpect(jsonPath("$.content[1].content").value("새로운 게시글이 달콤 태그와 작성되었습니다."))
        .andDo(print());
  }
}