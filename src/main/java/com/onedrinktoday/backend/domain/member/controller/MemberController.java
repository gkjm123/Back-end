package com.onedrinktoday.backend.domain.member.controller;

import com.onedrinktoday.backend.domain.member.dto.MemberRequest;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<String> signIn(@Valid @RequestBody MemberRequest.SignIn request) {

    return ResponseEntity.ok(memberService.signIn(request));
  }




}
