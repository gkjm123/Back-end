package com.onedrinktoday.backend.domain.member.controller;

import com.onedrinktoday.backend.domain.member.dto.MemberRequest;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.dto.PasswordResetDTO;
import com.onedrinktoday.backend.domain.member.dto.PasswordResetRequest;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/members/signup")
  public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody MemberRequest.SignUp request) {

    return ResponseEntity.ok(memberService.signUp(request));
  }

  @PostMapping("/members/signin")
  public ResponseEntity<String> signIn(@Valid @RequestBody MemberRequest.SignIn request) {

    return ResponseEntity.ok(memberService.signIn(request));
  }

  @PostMapping("/members/request-password-reset")
  public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
    memberService.requestPasswordReset(request.getEmail());
    return ResponseEntity.ok().build();
  }

  // 비밀번호 재설정 페이지로 리디렉션
  @GetMapping("/members/reset-password")
  public ResponseEntity<String> showResetPasswordPage(@RequestParam String token) {
    // 실제로 HTML 템플릿을 렌더링, 프론트엔드 통합테스트 전에 예시로 텍스트+토큰 반환
    return ResponseEntity.ok("비밀번호 재설정 페이지. 토큰: " + token);
  }

  @PostMapping("/members/reset-password")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDTO request) {
    memberService.resetPassword(request.getToken(), request.getNewPassword());
    return ResponseEntity.ok().build();
  }

}
