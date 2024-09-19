package com.onedrinktoday.backend.domain.announcement.service;

import static com.onedrinktoday.backend.global.exception.ErrorCode.ACCESS_DENIED;
import static com.onedrinktoday.backend.global.exception.ErrorCode.ANNOUNCEMENT_NOT_FOUND;

import com.onedrinktoday.backend.domain.announcement.dto.AnnouncementRequest;
import com.onedrinktoday.backend.domain.announcement.dto.AnnouncementResponse;
import com.onedrinktoday.backend.domain.announcement.entity.Announcement;
import com.onedrinktoday.backend.domain.announcement.repository.AnnouncementRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

  private final AnnouncementRepository announcementRepository;
  private final MemberService memberService;

  public AnnouncementResponse createAnnouncement(AnnouncementRequest announcementRequest) {
    Member member = memberService.getMember();

    Announcement announcement = Announcement.builder()
        .member(member)
        .title(announcementRequest.getTitle())
        .content(announcementRequest.getContent())
        .imageUrl(announcementRequest.getImageUrl())
        .build();

    announcement = announcementRepository.save(announcement);

    return AnnouncementResponse.from(announcement);
  }

  public Page<AnnouncementResponse> getAllAnnouncements(Pageable pageable) {
    Page<Announcement> announcements = announcementRepository.findAll(pageable);

    if (announcements.isEmpty()) {
      return Page.empty(pageable);
    }

    return announcementRepository.findAll(pageable)
        .map(AnnouncementResponse::from);
  }

  public AnnouncementResponse getAnnouncement(Long announcementId) {
    Announcement announcement = announcementRepository.findById(announcementId)
        .orElseThrow(() -> new CustomException(ANNOUNCEMENT_NOT_FOUND));

    return AnnouncementResponse.from(announcement);
  }

  public AnnouncementResponse updateAnnouncement(Long announcementId,
      AnnouncementRequest announcementRequest) {
    Member member = memberService.getMember();

    Announcement announcement = announcementRepository.findById(announcementId)
        .orElseThrow(() -> new CustomException(ANNOUNCEMENT_NOT_FOUND));

    if (!announcement.getMember().equals(member)) {
      throw new CustomException(ACCESS_DENIED);
    }

    String updatedTitle =
        (announcementRequest.getTitle() != null && !announcementRequest.getTitle().trim().isEmpty())
            ? announcementRequest.getTitle()
            : announcement.getTitle();
    String updatedContent =
        (announcementRequest.getContent() != null && !announcementRequest.getContent().trim()
            .isEmpty())
            ? announcementRequest.getContent()
            : announcement.getContent();
    String updatedImageUrl =
        (announcementRequest.getImageUrl() != null && !announcementRequest.getImageUrl().trim()
            .isEmpty())
            ? announcementRequest.getImageUrl()
            : announcement.getImageUrl();

    Announcement updatedAnnouncement = Announcement.builder()
        .id(announcement.getId())
        .member(announcement.getMember())
        .title(updatedTitle)
        .content(updatedContent)
        .imageUrl(updatedImageUrl)
        .createdAt(announcement.getCreatedAt())
        .updatedAt(announcement.getUpdatedAt())
        .build();

    announcementRepository.save(updatedAnnouncement);

    return AnnouncementResponse.from(updatedAnnouncement);
  }

  public void deleteAnnouncement(Long announcementId) {
    Member member = memberService.getMember();

    Announcement announcement = announcementRepository.findById(announcementId)
        .orElseThrow(() -> new CustomException(ANNOUNCEMENT_NOT_FOUND));

    if (!announcement.getMember().equals(member)) {
      throw new CustomException(ACCESS_DENIED);
    }

    announcementRepository.delete(announcement);
  }
}
