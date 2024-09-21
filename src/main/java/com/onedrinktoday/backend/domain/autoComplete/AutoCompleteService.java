package com.onedrinktoday.backend.domain.autoComplete;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutoCompleteService {

  private final RedisTemplate<String, String> redisTemplate;
  private final DrinkRepository drinkRepository;

  public void saveAutoCompleteTag(String tag) {

    for (int i = 0; i < tag.length(); i++) {
      redisTemplate.opsForZSet().add("auto-tags", tag.substring(0, i + 1), 0);
    }

    redisTemplate.opsForZSet().add("auto-tags", tag + "*", 0);
  }

  public void saveAutoCompleteDrink(String drink) {

    for (int i = 0; i < drink.length(); i++) {
      redisTemplate.opsForZSet().add("auto-drinks", drink.substring(0, i + 1), 0);
    }

    redisTemplate.opsForZSet().add("auto-drinks", drink + "*", 0);
  }

  public List<String> getAutoCompleteTag(String tag) {

    Long index = redisTemplate.opsForZSet().rank("auto-tags", tag);

    if (index == null) {
      return Collections.emptyList();
    }

    Set<String> tags = redisTemplate.opsForZSet().range("auto-tags", index, index + 50);

    return tags.stream()
        .filter(x -> x.startsWith(tag) && x.endsWith("*"))
        .map(x -> x.substring(0, x.length()-1)).toList();
  }

  public List<String> getAutoCompleteDrink(String drink) {

    Long index = redisTemplate.opsForZSet().rank("auto-drinks", drink);

    if (index == null) {
      return Collections.emptyList();
    }

    Set<String> drinks = redisTemplate.opsForZSet().range("auto-drinks", index, index + 50);

    return drinks.stream()
        .filter(x -> x.startsWith(drink) && x.endsWith("*"))
        .map(x -> x.substring(0, x.length()-1)).toList();
  }

  @Cacheable(key = "#regionId.toString().concat(':').concat(#name)", value = "drink-complete")
  public List<String> getAutoCompleteRegionDrink(Long regionId, String name) {

    return drinkRepository.findAllByRegion_IdAndNameStartsWith(regionId, name)
        .stream().map(Drink::getName).toList();
  }

  @Scheduled(cron = "0 0 0 * * *")
  @CacheEvict(value = "drink-complete", allEntries = true)
  public void clearAutoComplete() {}

}
