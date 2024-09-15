package com.onedrinktoday.backend.domain.suggest.service;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.suggest.repository.SuggestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestMonthlyService {
  private final SuggestRepository suggestRepository;
  private final SendMonthlyEmailService emailService;
  private final MemberRepository memberRepository;

  // 매월 1일 특산주 추천
  @Scheduled(cron = "0 0 0 1 * *")
  public void sendMonthlyDrinkSuggestion() {
    List<Member> allMembers = memberRepository.findAll();

    for (Member member : allMembers) {
      // 회원 지역 정보 있는지 확인
      if (member.getRegion() != null) {
        List<Drink> suggestDrink = suggestRepository.findRandomDrink(member.getRegion().getId(), PageRequest.of(0, 3));

        emailService.sendMonthlyDrinkEmail(member, suggestDrink);
      }
    }
  }
}
