package com.onedrinktoday.backend.domain.member.controller;

import com.onedrinktoday.backend.domain.member.dto.ChangePasswordRequestDTO;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.dto.PasswordResetRequest;
import com.onedrinktoday.backend.domain.member.dto.UpdateProfileRequest;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.global.security.JwtProvider;
import com.onedrinktoday.backend.global.security.TokenDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

  private final MemberService memberService;
  private final JwtProvider jwtProvider;

  @GetMapping("/members/email/{email}/validation")
  public ResponseEntity<String> validateEmail(@PathVariable String email) {
    memberService.validateEmail(email);
    return ResponseEntity.ok().body("이메일 중복 확인 완료");
  }

  @PostMapping("/members/signup")
  public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody MemberRequest.SignUp request) {

    return ResponseEntity.ok(memberService.signUp(request));
  }

  @PostMapping("/members/signin")
  public ResponseEntity<TokenDTO> signIn(@Valid @RequestBody MemberRequest.SignIn request) {

    return ResponseEntity.ok(memberService.signIn(request));
  }

  @PostMapping("/members/request-password-reset")
  public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
    memberService.requestPasswordReset(request.getEmail());
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
  public ResponseEntity<TokenDTO> refreshAccessToken(
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

  @PutMapping("/members/profile")
  public ResponseEntity<MemberResponse> updateMemberProfile(
      @RequestBody UpdateProfileRequest request
  ) {
    return ResponseEntity.ok(memberService.updateMemberProfile(request.getUrl()));
  }

  @DeleteMapping("/members")
  public ResponseEntity<String> withdrawMember() {
    memberService.withdrawMember();
    return ResponseEntity.ok("회원 탈퇴 완료");
  }
}