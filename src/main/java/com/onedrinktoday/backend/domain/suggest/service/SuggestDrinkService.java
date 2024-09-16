package com.onedrinktoday.backend.domain.suggest.service;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.suggest.repository.SuggestRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestDrinkService {
  private final SuggestRepository suggestRepository;

  // 최근 1주일 상위 20개 특산주 중 랜덤 15개 반환
  public List<DrinkResponse> suggestTopDrinks() {
    LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

    // 최근 1주일간 게시글에 등록된 상위 20개 인기 특산주 반환
    List<Object[]> topDrinksData = suggestRepository.findTopDrinksByPostCountInLastWeek(oneWeekAgo, PageRequest.of(0, 20));

    List<Drink> topDrinks = topDrinksData.stream()
        .map(result -> (Drink) result[0])
        .collect(Collectors.toList());

    // 상위 20개 특산주 중 랜덤 15개 선택
    Collections.shuffle(topDrinks);
    List<Drink> randomTopDrinks = topDrinks.stream().limit(15).collect(Collectors.toList());

    return randomTopDrinks.stream()
        .map(DrinkResponse::from)
        .collect(Collectors.toList());
  }
}
