package com.onedrinktoday.backend.domain.suggest.service;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.suggest.repository.SuggestRepository;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestBirthDateService {
  private final SuggestRepository suggestRepository;
  private final SendBirthDateEmailService emailService;

  // 생일이 오늘인 사용자 대상 특산주 추천
  @Scheduled(cron = "0 0 0 * * *")
  public void sendBirthDateDrinkSuggestion() {
    List<Member> membersWithBirthDate = suggestRepository.findAllByBirthDate(new Date());

    for (Member member : membersWithBirthDate) {
      // 회원 거주지 특산주 3개 랜덤 추천
      List<Drink> suggestDrink = suggestRepository.findRandomDrink(member.getRegion().getId(), Pageable.ofSize(3));

      emailService.sendBirthDateEmail(member, suggestDrink);
    }
  }
}
