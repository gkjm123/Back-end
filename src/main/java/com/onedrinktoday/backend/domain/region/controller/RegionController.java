package com.onedrinktoday.backend.domain.region.controller;

import com.onedrinktoday.backend.domain.region.dto.RegionRequest;
import com.onedrinktoday.backend.domain.region.dto.RegionResponse;
import com.onedrinktoday.backend.domain.region.service.RegionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RegionController {

  private final RegionService regionService;

  @PostMapping("/regions")
  public ResponseEntity<RegionResponse> createRegion(@RequestBody RegionRequest request) {

    return ResponseEntity.ok(regionService.createRegion(request));
  }

  @GetMapping("/regions")
  public ResponseEntity<List<RegionResponse>> getRegions() {

    return ResponseEntity.ok(regionService.getRegions());
  }

  @GetMapping("/regions/{regionId}")
  public ResponseEntity<RegionResponse> getRegion(@PathVariable Long regionId) {

    return ResponseEntity.ok(regionService.getRegion(regionId));
  }
}
