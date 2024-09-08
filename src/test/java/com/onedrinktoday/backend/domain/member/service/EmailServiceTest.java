package com.onedrinktoday.backend.domain.member.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

  @InjectMocks
  private EmailService emailService;

  @Mock
  private JavaMailSender emailSender;

  static final String to = "john.doe@example.com";
  static final String resetLink = "http://localhost:8080/api/members/reset-password?token=resetToken";

  @Test
  @DisplayName("이메일 전송 성공")
  void successSendEmail() {
    // Given
    // When
    emailService.sendPasswordResetEmail(to, resetLink);

    // Then
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("비밀번호 재설정 요청");
    message.setText("비밀번호를 재설정하려면 다음 링크를 클릭하세요: " + resetLink);

    verify(emailSender, times(1)).send(message);
  }

  @Test
  @DisplayName("이메일 전송 실패 - 이메일을 전송하는 로직 null")
  void failSendEmail() {
    // Given
    EmailService emailService = new EmailService(null);

    // When
    // Then
    assertThrows(NullPointerException.class, () -> emailService.sendPasswordResetEmail(to, resetLink));
  }
}