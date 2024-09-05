package com.onedrinktoday.backend.domain.member.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

   private final JavaMailSender emailSender;

   public EmailService(JavaMailSender emailSender) {
     this.emailSender = emailSender;
   }

   public void sendPasswordResetEmail(String to, String resetLink) {
     SimpleMailMessage message = new SimpleMailMessage();
     message.setTo(to);
     message.setSubject("비밀번호 재설정 요청");
     message.setText("비밀번호를 재설정하려면 다음 링크를 클릭하세요: " + resetLink);
     emailSender.send(message);
 }
}
