package com.onedrinktoday.backend.domain.member.controller;

import com.onedrinktoday.backend.domain.member.dto.MemberRequest;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.global.security.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public ResponseEntity<TokenDto> signIn(@Valid @RequestBody MemberRequest.SignIn request) {

    return ResponseEntity.ok(memberService.signIn(request));
  }

  //access 토큰 만료시 FE 에서 요청하는 컨트롤러, Refresh Token 을 헤더로 받아, 엑세스 토큰 새로 발급해 반환
  @PostMapping("/members/refresh")
  public ResponseEntity<TokenDto> refreshAccessToken(
      @RequestHeader("Refresh-Token") String refreshToken) {

    return ResponseEntity.ok(memberService.refreshAccessToken(refreshToken));
  }

  @GetMapping("/members/{memberId}")
  public ResponseEntity<MemberResponse> getMemberInfo(@PathVariable Long memberId) {

    return ResponseEntity.ok(memberService.getMemberInfo(memberId));
  }

  @PostMapping("members/{memberId}")
  public ResponseEntity<MemberResponse> updateMemberInfo(@PathVariable Long memberId,
      @Valid @RequestBody MemberRequest.UpdateInfo request) {

    return ResponseEntity.ok(memberService.updateMemberInfo(memberId, request));
  }
}