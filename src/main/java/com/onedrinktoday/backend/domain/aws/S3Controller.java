package com.onedrinktoday.backend.domain.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class S3Controller {

  private final S3Service s3Service;

  @PostMapping("/image")
  public ResponseEntity<String> uploadImage(@RequestParam("multipartFile") MultipartFile file) {
    return ResponseEntity.ok(s3Service.uploadAndGetUrl(file));
  }
}
