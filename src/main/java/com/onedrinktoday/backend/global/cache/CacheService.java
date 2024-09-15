package com.onedrinktoday.backend.global.cache;

import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

  private final PostRepository postRepository;

  @Cacheable(key = "#drinkId", value = "avg-rating")
  public Double getAverageRating(Long drinkId) {
    return postRepository.getAverageRating(drinkId);
  }
}
