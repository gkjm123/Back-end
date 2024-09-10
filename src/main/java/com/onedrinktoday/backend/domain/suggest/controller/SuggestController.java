package com.onedrinktoday.backend.domain.suggest.controller;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.domain.suggest.service.SuggestBirthDateService;
import com.onedrinktoday.backend.domain.suggest.service.SuggestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SuggestController {
  private final SuggestService suggestService;
  private final MemberService memberService;
  private final SuggestBirthDateService suggestBirthDateService;

  @GetMapping("/suggest/drink")
  public ResponseEntity<DrinkResponse> suggestDrink(@RequestParam Float lat, @RequestParam Float lon) {
    DrinkResponse suggestDrink = suggestService.suggestDrinkByLocation(lat, lon);
    return ResponseEntity.ok(suggestDrink);
  }

  // 생일 자정 이메일 전송
  @Scheduled(cron = "0 0 0 * * *")
  public void sendBirthdayDrinkSuggestion() {
    suggestBirthDateService.sendBirthDateDrinkSuggestion();
  }
}
