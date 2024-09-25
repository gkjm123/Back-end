package com.onedrinktoday.backend.domain.member.controller;

import com.onedrinktoday.backend.domain.member.dto.PasswordResetDTO;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class PasswordResetController {

  private final MemberService memberService;

  // 비밀번호 재설정 페이지 렌더링
  @GetMapping("/members/password-reset")
  public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
    model.addAttribute("token", token);
    return "passwordReset";
  }

  // 비밀번호 재설정
  @PostMapping("/members/password-reset")
  public String resetPassword(@Valid @ModelAttribute PasswordResetDTO request, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      String errorMessage = Objects.requireNonNull(bindingResult.getFieldError("newPassword")).getDefaultMessage();
      model.addAttribute("error", errorMessage);  // 에러 메시지 추가
      model.addAttribute("token", request.getToken());
      return "passwordReset";  // 에러 메시지와 함께 비밀번호 재설정 페이지 다시 렌더링
    }

    // 비밀번호 변경
    memberService.resetPassword(request.getToken(), request.getNewPassword());

    // 비밀번호 변경 성공 후 성공 페이지로 리디렉션
    return "redirect:/api/members/password-reset-success";
  }

  // 비밀번호 재설정 성공 페이지 렌더링
  @GetMapping("/members/password-reset-success")
  public String showPasswordResetSuccessPage() {
    return "passwordResetSuccess";  // 성공 페이지 템플릿 반환
  }
}