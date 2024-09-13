package com.onedrinktoday.backend.domain.notification.controller;

import com.onedrinktoday.backend.domain.notification.dto.NotificationResponse;
import com.onedrinktoday.backend.domain.notification.service.NotificationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/notifications")
  public List<NotificationResponse> getRecentNotifications() {

    return notificationService.getRecentNotifications()
        .stream()
        .map(NotificationResponse::from)
        .collect(Collectors.toList());
  }
}