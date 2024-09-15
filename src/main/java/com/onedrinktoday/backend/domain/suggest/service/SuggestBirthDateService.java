package com.onedrinktoday.backend.domain.suggest.service;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.suggest.repository.SuggestRepository;
import java.time.LocalDate;
import java.util.ArrayList;
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

  // 1일 생일자 리스트 저장
  private List<Long> birthdayOneday = new ArrayList<>();

  // 생일이 오늘인 사용자 대상 특산주 추천
  @Scheduled(cron = "0 0 0 * * *")
  public void sendBirthDateDrinkSuggestion() {
    sendBirthDateDrinkSuggestionForDate(LocalDate.now());
  }

  // 특정 날짜에 생일인 사용자 대상으로 특산주 추천(테스트용)
  public void sendBirthDateDrinkSuggestionForDate(LocalDate date) {
    birthdayOneday.clear();  // 생일자 리스트 초기화

    int month = date.getMonthValue();
    int day = date.getDayOfMonth();

    // 특정 날짜 기준으로 생일인 사용자 조회
    List<Member> membersWithBirthDate = suggestRepository.findAllByBirthDate(month, day);

    for (Member member : membersWithBirthDate) {
      // 회원 거주지 특산주 3개 랜덤 추천
      List<Drink> suggestDrink = suggestRepository.findRandomDrink(member.getRegion().getId(), Pageable.ofSize(3));
      emailService.sendBirthDateEmail(member, suggestDrink);

      // 생일이 1일인 사용자 리스트 추가
      if (date.getDayOfMonth() == 1) {
        birthdayOneday.add(member.getId());
      }
    }
  }

  // 매월 1일 생일자 목록 반환하는 메서드
  public List<Long> getBirthdayOnedayList() {
    return new ArrayList<>(birthdayOneday);
  }
}
