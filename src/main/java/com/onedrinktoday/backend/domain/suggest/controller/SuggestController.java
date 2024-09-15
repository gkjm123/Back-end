package com.onedrinktoday.backend.domain.suggest.controller;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.suggest.service.SuggestBirthDateService;
import com.onedrinktoday.backend.domain.suggest.service.SuggestMonthlyService;
import com.onedrinktoday.backend.domain.suggest.service.SuggestService;
import com.onedrinktoday.backend.domain.suggest.service.SuggestTagService;
import com.onedrinktoday.backend.domain.tag.dto.TagDTO;
import java.util.List;
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
  private final SuggestBirthDateService suggestBirthDateService;
  private final SuggestMonthlyService suggestMonthlyService;
  private final SuggestTagService suggestTagService;

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

  // 매월 1일 이메일 전송
  @Scheduled(cron = "0 0 0 1 * *")
  public void sendMonthlyDrinkSuggestion() {
    suggestMonthlyService.sendMonthlyDrinkSuggestion();
  }

  // 인기 태그 15개 조회
  @GetMapping("/suggest/tags")
  public ResponseEntity<List<TagDTO>> suggestRandomTags() {
    List<TagDTO> randomTags = suggestTagService.getRandomTopTags();
    return ResponseEntity.ok(randomTags);
  }
}
