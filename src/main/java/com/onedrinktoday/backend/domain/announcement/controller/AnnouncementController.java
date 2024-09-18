package com.onedrinktoday.backend.domain.announcement.controller;

import com.onedrinktoday.backend.domain.announcement.dto.AnnouncementRequest;
import com.onedrinktoday.backend.domain.announcement.dto.AnnouncementResponse;
import com.onedrinktoday.backend.domain.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnnouncementController {

  private final AnnouncementService announcementService;

  @PreAuthorize("hasRole('MANAGER')")
  @PostMapping("/announcements")
  public ResponseEntity<AnnouncementResponse> createAnnouncement(
      @RequestBody AnnouncementRequest announcementRequest) {
    return ResponseEntity.ok(announcementService.createAnnouncement(announcementRequest));
  }

  @GetMapping("/announcements")
  public ResponseEntity<Page<AnnouncementResponse>> getAllAnnouncements(
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<AnnouncementResponse> announcements = announcementService.getAllAnnouncements(pageable);
    return ResponseEntity.ok(announcements);
  }

  @GetMapping("/announcements/{announcementId}")
  public ResponseEntity<AnnouncementResponse> getAnnouncement(@PathVariable Long announcementId) {
    return ResponseEntity.ok(announcementService.getAnnouncement(announcementId));
  }

  @PreAuthorize("hasRole('MANAGER')")
  @PutMapping("/announcements/{announcementId}")
  public ResponseEntity<AnnouncementResponse> updateAnnouncement(
      @PathVariable Long announcementId,
      @RequestBody AnnouncementRequest announcementRequest) {
    return ResponseEntity.ok(
        announcementService.updateAnnouncement(announcementId, announcementRequest));
  }

  @PreAuthorize("hasRole('MANAGER')")
  @DeleteMapping("/announcements/{announcementId}")
  public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long announcementId) {
    announcementService.deleteAnnouncement(announcementId);
    return ResponseEntity.noContent().build();
  }

}
