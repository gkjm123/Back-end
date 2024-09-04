package com.onedrinktoday.backend.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class GoogleController {

  @Value("${oauth2.google.login-uri}")
  private String loginUri;

  private final GoogleService googleService;

  @PostMapping("/google/join")
  public ResponseEntity<TokenDto> join(@RequestParam String code) {
    return ResponseEntity.ok(googleService.join(code));
  }

  @GetMapping("/google/login-uri")
  public ResponseEntity<String> getLoginUri() {
    return ResponseEntity.ok(loginUri);
  }
}
