package com.onedrinktoday.backend.domain.member.controller;

import com.onedrinktoday.backend.domain.member.dto.ChangePasswordRequestDTO;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.dto.PasswordResetDTO;
import com.onedrinktoday.backend.domain.member.dto.PasswordResetRequest;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.global.security.JwtProvider;
import com.onedrinktoday.backend.global.security.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

  private final MemberService memberService;
  private final JwtProvider jwtProvider;

  @PostMapping("/members/signup")
  public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody MemberRequest.SignUp request) {

    return ResponseEntity.ok(memberService.signUp(request));
  }

  @PostMapping("/members/signin")
  public ResponseEntity<TokenDto> signIn(@Valid @RequestBody MemberRequest.SignIn request) {

    return ResponseEntity.ok(memberService.signIn(request));
  }

  @PostMapping("/members/request-password-reset")
  public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
    memberService.requestPasswordReset(request.getEmail());
    return ResponseEntity.ok().build();
  }

  // 비밀번호 재설정 페이지로 리디렉션(비밀번호 모를 경우)
  @GetMapping("/members/password-reset")
  public ResponseEntity<String> showResetPasswordPage(@RequestParam String token) {
    // 실제로 HTML 템플릿을 렌더링, 프론트엔드 통합테스트 전에 예시로 텍스트+토큰 반환
    return ResponseEntity.ok("비밀번호 재설정 페이지. 토큰: " + token);
  }

  // 비밀번호 모를 경우 재설정
  @PostMapping("/members/password-reset")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDTO request) {
    memberService.resetPassword(request.getToken(), request.getNewPassword());
    return ResponseEntity.ok().build();
  }

  // 비밀번호 알 경우 재설정
  @PostMapping("/members/password-change")
  public ResponseEntity<Void> changePassword(@RequestHeader("Access-Token") String token, @RequestBody ChangePasswordRequestDTO request) {

    String email = jwtProvider.getEmail(token);

    memberService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());

    return ResponseEntity.ok().build();
  }

  //access 토큰 만료시 FE 에서 요청하는 컨트롤러, Refresh Token 을 헤더로 받아, 엑세스 토큰 새로 발급해 반환
  @PostMapping("/members/refresh")
  public ResponseEntity<TokenDto> refreshAccessToken(
      @RequestHeader("Refresh-Token") String refreshToken) {

    return ResponseEntity.ok(memberService.refreshAccessToken(refreshToken));
  }

  @GetMapping("/members")
  public ResponseEntity<MemberResponse> getMemberInfo() {

    return ResponseEntity.ok(memberService.getMemberInfo());
  }

  @PostMapping("/members")
  public ResponseEntity<MemberResponse> updateMemberInfo(
      @Valid @RequestBody MemberRequest.UpdateInfo request) {

    return ResponseEntity.ok(memberService.updateMemberInfo(request));
  }
}