package com.onedrinktoday.backend.domain.suggest.service;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.member.entity.Member;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendMonthlyEmailService {
  private final JavaMailSender emailSender;

  public SendMonthlyEmailService(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }

  // 매월 1일 전송할 특산주 추천 이메일
  public void sendMonthlyDrinkEmail(Member member, List<Drink> drinks) {
    String subject = "매월 추천드리는 특산주 3종!";
    String body = createMonthlyEmailBody(member.getName(), drinks);

    sendEmail(member.getEmail(), subject, body);
  }

  private void sendEmail(String to, String subject, String body) {
    MimeMessage message = emailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, true);

      emailSender.send(message);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  private String createMonthlyEmailBody(String name, List<Drink> drinks) {
    StringBuilder sb = new StringBuilder();
    sb.append("<h1>").append(name).append("님을 위한 이 달의 특산주 3종 추천!</h1>");
    sb.append("<p>이번 달 추천드리는 특산주 3종입니다:</p>");
    sb.append("<ul>");

    for (Drink drink : drinks) {
      sb.append("<li>")
          .append("<b>").append(drink.getName()).append("</b><br>")
          .append("설명: ").append(drink.getDescription()).append("<br>")
          .append("도수: ").append(drink.getDegree()).append("도<br>")
          .append("당도: ").append(drink.getSweetness()).append("<br>")
          .append("가격: ").append(drink.getCost()).append("원<br>")
          .append("</li>");
    }

    sb.append("</ul>");
    sb.append("<p>즐거운 한 달 되세요!</p>");

    return sb.toString();
  }
}