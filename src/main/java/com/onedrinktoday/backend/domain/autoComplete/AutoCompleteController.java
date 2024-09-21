package com.onedrinktoday.backend.domain.autoComplete;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AutoCompleteController {

  private final AutoCompleteService autoCompleteService;

  @GetMapping("/auto-complete/tag")
  public ResponseEntity<List<String>> getAutoCompleteTag(@RequestParam String name) {
    return ResponseEntity.ok(autoCompleteService.getAutoCompleteTag(name));
  }

  @GetMapping("/auto-complete/drink")
  public ResponseEntity<List<String>> getAutoCompleteDrink(@RequestParam String name) {
    return ResponseEntity.ok(autoCompleteService.getAutoCompleteDrink(name));
  }

  @GetMapping("/auto-complete/region-drink")
  public ResponseEntity<List<String>> getAutoCompleteRegionDrink(
      @RequestParam Long regionId,
      @RequestParam String name
  ) {
    return ResponseEntity.ok(autoCompleteService.getAutoCompleteRegionDrink(regionId, name));
  }

}