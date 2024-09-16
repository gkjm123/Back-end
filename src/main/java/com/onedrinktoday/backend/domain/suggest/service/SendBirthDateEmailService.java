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
public class SendBirthDateEmailService {

  private final JavaMailSender emailSender;

  public SendBirthDateEmailService(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }

  public void sendBirthDateEmail(Member member, List<Drink> drinks) {
    String subject = "귀하의 생일을 진심으로 축하드립니다! 지역 특산주 3종을 추천드립니다!";
    String body = createBirthdayEmailBody(member.getName(), drinks);

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

  private String createBirthdayEmailBody(String name, List<Drink> drinks) {
    StringBuilder sb = new StringBuilder();
    sb.append("<h1>생일 축하드립니다, ").append(name).append("님!</h1>");
    sb.append("<p>지역의 특산주 3종을 추천드립니다:</p>");
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
    sb.append("<p>즐거운 하루 되세요!</p>");

    return sb.toString();
  }
}
