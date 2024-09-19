package com.onedrinktoday.backend.domain.announcement.service;

import static com.onedrinktoday.backend.global.exception.ErrorCode.ACCESS_DENIED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedrinktoday.backend.domain.announcement.dto.AnnouncementRequest;
import com.onedrinktoday.backend.domain.announcement.dto.AnnouncementResponse;
import com.onedrinktoday.backend.domain.announcement.entity.Announcement;
import com.onedrinktoday.backend.domain.announcement.repository.AnnouncementRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AnnouncementServiceTest {

  @InjectMocks
  private AnnouncementService announcementService;

  @Mock
  private MemberService memberService;

  @Mock
  private AnnouncementRepository announcementRepository;

  private AnnouncementRequest announcementRequest;
  private Announcement announcement;
  private Member member;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .name("John Doe")
        .build();

    announcement = Announcement.builder()
        .id(1L)
        .member(member)
        .title("공지사항 제목")
        .content("공지사항 내용입니다.")
        .imageUrl("http://image")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    announcementRequest = AnnouncementRequest.builder()
        .title("공지사항 제목")
        .content("공지사항 내용입니다.")
        .build();
  }

  @Test
  @DisplayName("공지사항 생성 성공")
  void successCreateAnnouncement() {
    //given
    when(memberService.getMember()).thenReturn(member);

    ArgumentCaptor<Announcement> argumentCaptor = ArgumentCaptor.forClass(Announcement.class);
    when(announcementRepository.save(argumentCaptor.capture()))
        .thenReturn(announcement);

    //when
    AnnouncementResponse response = announcementService.createAnnouncement(announcementRequest);

    //then
    Announcement capturedAnnouncement = argumentCaptor.getValue();
    assertEquals(announcementRequest.getTitle(), capturedAnnouncement.getTitle());
    assertEquals(announcementRequest.getContent(), capturedAnnouncement.getContent());
    assertEquals(announcement.getId(), response.getId());
    assertEquals(announcement.getTitle(), response.getTitle());
    assertEquals(announcement.getContent(), response.getContent());
    assertEquals(announcement.getImageUrl(), response.getImageUrl());
  }

  @Test
  @DisplayName("공지사항 생성 실패 - 사용자 인증 실패")
  void failCreateAnnouncement() {
    //given
    when(memberService.getMember()).thenThrow(new CustomException(ACCESS_DENIED));

    //when
    CustomException exception = assertThrows(CustomException.class, () -> announcementService.createAnnouncement(new AnnouncementRequest("title", "content")));

    //then
    assertEquals(ACCESS_DENIED.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("공지사항 조회 성공")
  void successGetAnnouncement() {
    //given
    when(announcementRepository.findById(1L)).thenReturn(Optional.of(announcement));

    //when
    AnnouncementResponse response = announcementService.getAnnouncement(1L);

    //then
    assertEquals(announcement.getId(), response.getId());
    assertEquals(announcement.getTitle(), response.getTitle());
    assertEquals(announcement.getContent(), response.getContent());
    assertEquals(announcement.getImageUrl(), response.getImageUrl());
  }

  @Test
  @DisplayName("공지사항 조회 실패 - 공지사항 없음")
  void failGetAnnouncement() {
    // given
    when(announcementRepository.findById(1L)).thenReturn(Optional.empty());

    //when, then
    assertThrows(CustomException.class, () -> announcementService.getAnnouncement(1L));
  }

  @Test
  @DisplayName("공지사항 수정 성공")
  void successUpdateAnnouncement() {
    //given
    String newTitle = "수정된 공지사항 제목";
    String newContent = "수정된 공지사항 내용입니다.";

    AnnouncementRequest updateRequest = AnnouncementRequest.builder()
        .title(newTitle)
        .content(newContent)
        .build();

    Announcement updatedAnnouncement = Announcement.builder()
        .id(1L)
        .member(member)
        .title(newTitle)
        .content(newContent)
        .imageUrl(announcement.getImageUrl())
        .createdAt(announcement.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .build();

    when(memberService.getMember()).thenReturn(member);
    when(announcementRepository.findById(1L)).thenReturn(Optional.of(announcement));
    when(announcementRepository.save(any(Announcement.class))).thenReturn(updatedAnnouncement);

    //when
    AnnouncementResponse response = announcementService.updateAnnouncement(1L, updateRequest);

    //then
    ArgumentCaptor<Announcement> argumentCaptor = ArgumentCaptor.forClass(Announcement.class);
    verify(announcementRepository).save(argumentCaptor.capture());
    Announcement savedAnnouncement = argumentCaptor.getValue();

    assertEquals(newTitle, savedAnnouncement.getTitle());
    assertEquals(newContent, savedAnnouncement.getContent());
    assertEquals(announcement.getImageUrl(), savedAnnouncement.getImageUrl());
    assertEquals(newTitle, response.getTitle());
    assertEquals(newContent, response.getContent());
    assertEquals(announcement.getImageUrl(), response.getImageUrl());
  }

  @Test
  @DisplayName("공지사항 수정 실패 - 공지사항 없음")
  void failUpdateAnnouncement() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(announcementRepository.findById(1L)).thenReturn(Optional.empty());

    //when, then
    assertThrows(CustomException.class, () -> announcementService.updateAnnouncement(1L, announcementRequest));
  }

  @Test
  @DisplayName("공지사항 삭제 성공")
  void successDeleteAnnouncement() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(announcementRepository.findById(1L)).thenReturn(Optional.of(announcement));

    //when
    announcementService.deleteAnnouncement(1L);

    //then
    verify(announcementRepository).delete(announcement);
  }

  @Test
  @DisplayName("공지사항 삭제 실패 - 공지사항 없음")
  void failDeleteAnnouncement() {
    //given
    when(memberService.getMember()).thenReturn(member);
    when(announcementRepository.findById(1L)).thenReturn(Optional.empty());

    //when, then
    assertThrows(CustomException.class, () -> announcementService.deleteAnnouncement(1L));
  }
}