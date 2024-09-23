package com.onedrinktoday.backend.domain.suggest.controller;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.suggest.service.SuggestBirthDateService;
import com.onedrinktoday.backend.domain.suggest.service.SuggestDrinkService;
import com.onedrinktoday.backend.domain.suggest.service.SuggestMonthlyService;
import com.onedrinktoday.backend.domain.suggest.service.SuggestService;
import com.onedrinktoday.backend.domain.suggest.service.SuggestTagService;
import com.onedrinktoday.backend.domain.tag.dto.TagDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SuggestController {
  private final SuggestService suggestService;
  private final SuggestBirthDateService suggestBirthDateService;
  private final SuggestMonthlyService suggestMonthlyService;
  private final SuggestTagService suggestTagService;
  private final SuggestDrinkService suggestDrinkService;

  // 사용자 위치 기반 가장 가까운 지역 특산주 추천
  @GetMapping("/suggest/drink")
  public ResponseEntity<DrinkResponse> suggestDrink(@RequestHeader("memberId") Long memberId, @RequestParam Float lat, @RequestParam Float lon) {
    DrinkResponse suggestDrink = suggestService.suggestDrinkByLocation(memberId, lat, lon);
    return ResponseEntity.ok(suggestDrink);
  }

  // 재접속시 기존 사용자의 저장된 지역 특산주 추천
  @GetMapping("/suggest/drink/current")
  public ResponseEntity<DrinkResponse> suggestDrinkForCurrent(@RequestHeader("memberId") Long memberId) {
    DrinkResponse suggestDrink = suggestService.suggestDrinkByCurrentRegion(memberId);
    return ResponseEntity.ok(suggestDrink);
  }

  // 생일 메일 전송 (오늘 날짜 기준)
  @GetMapping("/suggest/birthday-suggestion")
  public ResponseEntity<Void> sendBirthdayDrinkSuggestion() {
    suggestBirthDateService.sendBirthDateDrinkSuggestion();
    return ResponseEntity.ok().build();
  }

  // 생일 메일 전송 (특정 날짜 기준 - 테스트용)
  @GetMapping("/suggest/birthday-suggestion/custom")
  public ResponseEntity<Void> sendBirthdayDrinkSuggestionForDate(@RequestParam String customDate) {
    LocalDate date = LocalDate.parse(customDate);
    suggestBirthDateService.sendBirthDateDrinkSuggestionForDate(date);
    return ResponseEntity.ok().build();
  }

  // 매월 1일 전송
  @GetMapping("/suggest/monthly-suggestion")
  public void sendMonthlyDrinkSuggestion() {
    suggestMonthlyService.sendMonthlyDrinkSuggestion();
  }

  // 인기 태그 15개 조회
  @GetMapping("/suggest/tags")
  public ResponseEntity<List<TagDTO>> suggestRandomTags() {
    List<TagDTO> randomTags = suggestTagService.getRandomTopTags();
    return ResponseEntity.ok(randomTags);
  }

  // 인기 특산주 15개 조회
  @GetMapping("/suggest/drinks")
  public ResponseEntity<List<DrinkResponse>> suggestTopDrinks() {
    List<DrinkResponse> topDrinks = suggestDrinkService.suggestTopDrinks();
    return ResponseEntity.ok(topDrinks);
  }
}
