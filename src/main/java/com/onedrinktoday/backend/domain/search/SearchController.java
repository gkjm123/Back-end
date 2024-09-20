package com.onedrinktoday.backend.domain.search;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.post.dto.PostResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;

  @PostMapping("/search/post/tags")
  public ResponseEntity<Page<PostResponse>> searchPostByTag(
      @PageableDefault Pageable pageable,
      @RequestBody List<String> tags
  ) {

    return ResponseEntity.ok(searchService.searchPostByTag(pageable, tags));
  }

  @PostMapping("/search/post/drinks")
  public ResponseEntity<Page<PostResponse>> searchPostByDrink(
      @PageableDefault Pageable pageable,
      @RequestParam String drink
  ) {

    return ResponseEntity.ok(searchService.searchPostByDrink(pageable, drink));
  }

  @GetMapping("/search/drinks")
  public ResponseEntity<List<DrinkResponse>> searchDrink(
      @RequestParam Long regionId,
      @RequestParam String drinkName
  ) {

    return ResponseEntity.ok(searchService.searchDrink(regionId, drinkName));
  }
}
